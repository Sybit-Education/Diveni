<template>
  <b-container>
    <b-row class="mt-5">
      <b-col>
        <h1>{{ $t("page.vote.title") }}</h1>
      </b-col>
      <b-col>
        <estimate-timer
          :start-timestamp="timerTimestamp"
          :pause-timer="estimateFinished"
          :duration="timerCountdownNumber"
        />
      </b-col>
    </b-row>
    <b-row class="d-flex justify-content-end horizontal">
      <b-col>
        <b-button
          v-b-modal.close-session-modal
          style="max-height: 40px"
          variant="danger"
          class="mt-4"
          @click="leaveMeeting"
        >
          {{ $t("page.vote.button.leave.label") }}
        </b-button>
      </b-col>
      <b-col class="d-flex justify-content-end">
        <rounded-avatar
          :color="hexColor"
          :asset-name="avatarAnimalAssetName"
          :show-name="true"
          :name="name"
        />
      </b-col>
    </b-row>
    <b-row v-if="isMobile">
      <mobile-story-title
        v-if="userStoryMode !== 'NO_US'"
        :card-set="voteSet"
        :index="index"
        :initial-stories="userStories"
        :edit-description="false"
      />
    </b-row>
    <b-row v-if="isStartVoting" class="my-5">
      <div v-if="isMobile">
        <flicking
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
      </div>
      <b-row v-else class="d-flex justify-content-between flex-wrap text-center">
        <b-col>
          <div class="overflow-auto" style="max-height: 500px">
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
          </div>
        </b-col>
      </b-row>
    </b-row>
    <b-row v-if="!isStartVoting && !votingFinished" class="my-5 text-center">
      <h1>{{ $t("page.vote.waiting") }}</h1>
      <b-icon-three-dots animation="fade" class="my-5" font-scale="4" />
    </b-row>
    <b-row
      v-if="votingFinished"
      class="my-1 d-flex justify-content-center flex-wrap overflow-auto"
      style="max-height: 500px"
    >
      <SessionMemberCard
        v-for="member of members"
        :key="member.memberID"
        :color="member.hexColor"
        :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
        :name="member.name"
        :estimation="member.currentEstimation"
        :estimate-finished="votingFinished"
        :highlight="highlightedMembers.includes(member.memberID) || highlightedMembers.length === 0"
      />
    </b-row>
    <b-row v-if="userStoryMode !== 'NO_US'" class="mt-5">
      <b-col md="6">
        <UserStorySumComponent class="ms-4" />
      </b-col>
    </b-row>
    <b-row v-if="userStoryMode !== 'NO_US' && !isMobile">
      <b-col class="mt-2">
        <div class="overflow-auto" style="height: 700px">
          <user-stories
            :card-set="voteSet"
            :show-estimations="true"
            :initial-stories="userStories"
            :show-edit-buttons="false"
            @selectedStory="onSelectedStory($event)"
          />
        </div>
      </b-col>

      <b-col class="mt-2">
        <user-story-descriptions
          :card-set="voteSet"
          :index="index"
          :initial-stories="userStories"
          :edit-description="false"
        />
      </b-col>
    </b-row>
    <b-col v-if="userStoryMode !== 'NO_US' && isMobile" class="mt-2">
      <div class="overflow-auto">
        <mobile-story-list
          :card-set="voteSet"
          :show-estimations="true"
          :initial-stories="userStories"
          :show-edit-buttons="false"
          @selectedStory="onSelectedStory($event)"
        />
      </div>
    </b-col>
    <notify-member-component />
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import RoundedAvatar from "../components/RoundedAvatar.vue";
import MemberVoteCard from "../components/MemberVoteCard.vue";
import Constants from "../constants";
import EstimateTimer from "../components/EstimateTimer.vue";
import SessionMemberCard from "../components/SessionMemberCard.vue";
import NotifyMemberComponent from "../components/NotifyMemberComponent.vue";
import Member from "../model/Member";
import confetti from "canvas-confetti";
import UserStories from "../components/UserStories.vue";
import UserStoryDescriptions from "../components/UserStoryDescriptions.vue";
import MobileStoryList from "../components/MobileStoryList.vue";
import MobileStoryTitle from "../components/MobileStoryTitle.vue";
import UserStorySumComponent from "@/components/UserStorySum.vue";

export default Vue.extend({
  name: "MemberVotePage",
  components: {
    RoundedAvatar,
    MemberVoteCard,
    EstimateTimer,
    SessionMemberCard,
    NotifyMemberComponent,
    UserStories,
    UserStoryDescriptions,
    MobileStoryList,
    MobileStoryTitle,
    UserStorySumComponent,
  },
  props: {
    memberID: { type: String, default: undefined },
    name: { type: String, default: undefined },
    hexColor: { type: String, default: undefined },
    avatarAnimalAssetName: { type: String, default: undefined },
    voteSetJson: { type: String, default: undefined },
    timerSecondsString: { type: String, default: undefined },
    userStoryMode: { type: String, default: undefined },
  },
  data() {
    return {
      index: 0,
      draggedVote: null,
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
    highlightedMembers() {
      return this.$store.state.highlightedMembers;
    },
    timerTimestamp() {
      return this.$store.state.timerTimestamp ? this.$store.state.timerTimestamp : "";
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
    votingFinished(isFinished) {
      if (isFinished && this.highlightedMembers.length === 0) {
        confetti({
          particleCount: 100,
          startVelocity: 50,
          spread: 100,
        });
      }
    },
  },
  created() {
    // window.addEventListener("beforeunload", this.sendUnregisterCommand);
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
    onSelectedStory($event) {
      this.index = $event;
    },
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
    goToLandingPage() {
      window.localStorage.removeItem("memberCookie");
      this.$router.push({ name: "LandingPage" });
    },
    leaveMeeting() {
      this.goToLandingPage();
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
