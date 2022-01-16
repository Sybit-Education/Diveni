<template>
  <b-container>
    <user-stories-sidebar
      :card-set="voteSet"
      :show-estimations="true"
      :initial-stories="userStories"
      :show-edit-buttons="false"
    />
    <b-row class="my-5 mx-2">
      <b-col>
        <h1>{{ title }}</h1>
      </b-col>
      <b-col>
        <estimate-timer
          :pause-timer="estimateFinished"
          :timer-triggered="triggerTimer"
          :timer="timerCountdownNumber"
          :start-timer-on-component-creation="false"
          :initial-timer="timerCountdownNumber"
        />
      </b-col>
    </b-row>
    <b-row class="justify-content-center">
      <rounded-avatar
        :color="hexColor"
        :asset-name="avatarAnimalAssetName"
        :show-name="true"
        :name="name"
      />
    </b-row>
    <b-row v-if="isStartVoting" class="my-5">
      <flicking
        v-if="isMobile"
        id="flicking"
        :options="{
          renderOnlyVisible: false,
          horizontal: true,
          align: 'center',
          bound: false,
          defaultIndex: 0,
          deceleration: 0.0005,
        }"
      >
        <member-vote-card
          v-for="(voteOption, index) in voteSet"
          :key="voteOption"
          :ref="`memberCard${voteOption}`"
          class="flicking-panel mx-2"
          :vote-option="voteOption"
          :index="index"
          :hex-color="hexColor"
          :dragged="voteOption == draggedVote"
          :is-mobile="true"
          @sentVote="onSendVote"
        />
      </flicking>
      <b-row v-else class="d-flex justify-content-between flex-wrap">
        <b-col>
          <member-vote-card
            v-for="(voteOption, index) in voteSet"
            :key="voteOption"
            :ref="`memberCard${voteOption}`"
            style="display: inline-block"
            class="flicking-panel m-2"
            :vote-option="voteOption"
            :index="index"
            :hex-color="hexColor"
            :dragged="voteOption == draggedVote"
            :is-mobile="false"
            @sentVote="onSendVote"
          />
        </b-col>
      </b-row>
    </b-row>
    <b-row v-if="!isStartVoting && !votingFinished" class="my-5 text-center">
      <b-icon-three-dots animation="fade" class="my-5" font-scale="4" />
      <h1>{{ waitingText }}</h1>
    </b-row>
    <b-row v-if="votingFinished" class="my-1 d-flex justify-content-center flex-wrap">
      <SessionMemberCard
        v-for="member of members"
        :key="member.memberID"
        :color="member.hexColor"
        :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
        :name="member.name"
        :estimation="member.currentEstimation"
        :estimate-finished="votingFinished"
        :highest="estimateHighest ? estimateHighest.memberID === member.memberID : false"
        :lowest="estimateHighest ? estimateLowest.memberID === member.memberID : false"
      />
    </b-row>
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import RoundedAvatar from "../components/RoundedAvatar.vue";
import MemberVoteCard from "../components/MemberVoteCard.vue";
import Constants from "../constants";
import UserStoriesSidebar from "../components/UserStoriesSidebar.vue";
import EstimateTimer from "../components/EstimateTimer.vue";
import SessionMemberCard from "../components/SessionMemberCard.vue";
import Member from "../model/Member";

export default Vue.extend({
  name: "MemberVotePage",
  components: {
    RoundedAvatar,
    MemberVoteCard,
    EstimateTimer,
    UserStoriesSidebar,
    SessionMemberCard,
  },
  props: {
    memberID: { type: String, default: undefined },
    name: { type: String, default: undefined },
    hexColor: { type: String, default: undefined },
    avatarAnimalAssetName: { type: String, default: undefined },
    voteSetJson: { type: String, default: undefined },
    timerSecondsString: { type: String, default: undefined },
  },
  data() {
    return {
      title: this.$t("page.vote.title"),
      draggedVote: null,
      waitingText: this.$t("page.vote.waiting"),
      voteSet: [] as string[],
      timerCountdownNumber: 0,
      triggerTimer: 0,
      estimateFinished: false,
    };
  },
  computed: {
    isMobile() {
      return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
        navigator.userAgent
      );
    },
    userStories() {
      return this.$store.state.userStories;
    },
    memberUpdates() {
      return this.$store.state.memberUpdates;
    },
    isStartVoting(): boolean {
      return this.memberUpdates.at(-1) === Constants.memberUpdateCommandStartVoting;
    },
    votingFinished(): boolean {
      return this.memberUpdates.at(-1) === Constants.memberUpdateCommandVotingFinished;
    },
    members() {
      return this.$store.state.members;
    },
    membersEstimated(): Member[] {
      return this.members.filter((member: Member) => member.currentEstimation !== null);
    },
    estimateHighest(): Member | null {
      if (this.membersEstimated.length < 1) {
        return null;
      }
      return this.membersEstimated.reduce((prev, current) =>
        this.voteSet.indexOf(prev.currentEstimation!) >
        this.voteSet.indexOf(current.currentEstimation!)
          ? prev
          : current
      );
    },
    estimateLowest(): Member | null {
      if (this.membersEstimated.length < 1) {
        return null;
      }
      return this.membersEstimated.reduce((prev, current) =>
        this.voteSet.indexOf(prev.currentEstimation!) <
        this.voteSet.indexOf(current.currentEstimation!)
          ? prev
          : current
      );
    },
  },
  watch: {
    memberUpdates(updates) {
      if (updates.at(-1) === Constants.memberUpdateCommandStartVoting) {
        this.draggedVote = null;
        this.estimateFinished = false;
        this.triggerTimer = (this.triggerTimer + 1) % 5;
      } else if (updates.at(-1) === Constants.memberUpdateCommandVotingFinished) {
        this.estimateFinished = true;
      } else if (updates.at(-1) === Constants.memberUpdateCloseSession) {
        this.goToJoinPage();
      }
    },
  },
  created() {
    window.addEventListener("beforeunload", this.sendUnregisterCommand);
    this.timerCountdownNumber = JSON.parse(this.timerSecondsString);
  },
  mounted() {
    if (
      this.memberID === undefined ||
      this.name === undefined ||
      this.hexColor === undefined ||
      this.avatarAnimalAssetName === undefined
    ) {
      this.goToJoinPage();
    }
    this.voteSet = JSON.parse(this.voteSetJson);
  },
  beforeDestroy() {
    this.sendUnregisterCommand();
  },
  methods: {
    onSendVote({ vote }) {
      this.draggedVote = vote;
      const endPoint = `${Constants.webSocketVoteRoute}`;
      this.$store.commit("sendViaBackendWS", { endPoint, data: vote });
    },
    sendUnregisterCommand() {
      const endPoint = `${Constants.webSocketUnregisterRoute}`;
      this.$store.commit("sendViaBackendWS", { endPoint, data: null });
      this.$store.commit("clearStore");
    },
    goToJoinPage() {
      this.$router.push({ name: "JoinPage" });
    },
    backendAnimalToAssetName(animal: string) {
      return Constants.avatarAnimalToAssetName(animal);
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#flicking {
  /* overflow:visible;  Add when fix is clear how to stay responsiv*/
  width: 100%;
}
</style>
