<template>
  <b-container id="member-vote-page">
    <b-overlay :show="pauseSession">
      <template #overlay>
        <b-spinner class="me-2" />
        <span class="overlayText">
          {{ $t("page.vote.hostLeft") }}
        </span>
      </template>

      <b-row class="mt-5">
        <b-col>
          <h1>{{ $t("page.vote.title") }}</h1>
        </b-col>
        <b-col cols="auto" class="mr-auto">
          <rounded-avatar :member="getMember" />
        </b-col>
        <b-col cols="auto">
          <session-leave-button />
          <estimate-timer
            v-if="timerTimestamp"
            class="mt-3"
            :start-timestamp="timerTimestamp"
            :pause-timer="estimateFinished || pauseSession"
            :duration="timerCountdownNumber"
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
              v-for="(voteOption, idx) in voteSet"
              :key="voteOption"
              :ref="`memberCard${voteOption}`"
              class="flicking-panel mx-2"
              :vote-option="voteOption"
              :index="idx"
              :hex-color="hexColor"
              :dragged="voteOption === draggedVote"
              :is-mobile="true"
              :disabled="pauseSession"
              @sentVote="onSendVote"
            />
          </flicking>
        </div>
        <b-row
          v-else
          class="d-flex justify-content-between flex-wrap text-center"
        >
          <b-col>
            <div class="overflow-auto" style="max-height: 500px">
              <member-vote-card
                v-for="(voteOption, idx) in voteSet"
                :key="voteOption"
                :ref="`memberCard${voteOption}`"
                style="display: inline-block"
                class="flicking-panel m-2"
                :vote-option="voteOption"
                :index="idx"
                :hex-color="hexColor"
                :dragged="voteOption === draggedVote"
                :is-mobile="false"
                :disabled="pauseSession"
                @sentVote="onSendVote"
              />
            </div>
          </b-col>
        </b-row>
      </b-row>
      <b-row v-if="!isStartVoting && !votingFinished" class="my-5">
        <h3>
          {{ $t("page.vote.waiting") }}
          <sub><b-icon-three-dots animation="fade" font-scale="1" /></sub>
        </h3>
      </b-row>
      <b-row
        v-if="votingFinished"
        class="my-1 d-flex justify-content-center flex-wrap overflow-auto"
        style="max-height: 500px"
      >
        <session-member-card
          v-for="member of members"
          :key="member.memberID"
          :member="member"
          :props="{
            estimateFinished: votingFinished,
            highlight:
              highlightedMembers.includes(member.memberID) ||
              highlightedMembers.length === 0,
          }"
        />
      </b-row>
      <b-row v-if="userStoryMode !== 'NO_US'" class="mt-5">
        <b-col md="6">
          <user-story-sum-component class="ms-4" />
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
      <notify-member-component
        @hostLeft="reactOnHostLeave"
        @hostJoined="reactOnHostJoin"
      />
    </b-overlay>
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
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
import SessionLeaveButton from "@/components/actions/SessionLeaveButton.vue";
import Flicking from "@egjs/vue3-flicking";
import { useToast } from "vue-toastification";

export default defineComponent({
  name: "MemberVotePage",
  components: {
    SessionLeaveButton,
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
    Flicking,
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
      pauseSession: false,
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
      return (
        this.memberUpdates.at(-1) === Constants.memberUpdateCommandStartVoting
      );
    },
    votingFinished(): boolean {
      return (
        this.memberUpdates.at(-1) ===
        Constants.memberUpdateCommandVotingFinished
      );
    },
    members() {
      return this.$store.state.members;
    },
    membersEstimated(): Member[] {
      return this.members.filter(
        (member: Member) => member.currentEstimation !== null
      );
    },
    highlightedMembers() {
      return this.$store.state.highlightedMembers;
    },
    timerTimestamp() {
      return this.$store.state.timerTimestamp
        ? this.$store.state.timerTimestamp
        : "";
    },
    notifications() {
      return this.$store.state.notifications;
    },
    getMember() {
      return {
        hexColor: this.hexColor,
        avatarAnimal: this.avatarAnimalAssetName,
        name: this.name,
      };
    },
  },
  watch: {
    memberUpdates(updates) {
      if (updates.at(-1) === Constants.memberUpdateCommandStartVoting) {
        this.draggedVote = null;
        this.estimateFinished = false;
        this.triggerTimer = (this.triggerTimer + 1) % 5;
      } else if (
        updates.at(-1) === Constants.memberUpdateCommandVotingFinished
      ) {
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
    notifications(notifications) {
      if (
        notifications.at(-1).type === "MEMBER_LEFT" &&
        notifications.at(-1).payload.memberID === this.memberID
      ) {
        useToast().error(
          this.$t("session.notification.messages.memberRemoved")
        );
        this.leaveMeeting();
      }
    },
  },
  created() {
    // window.addEventListener("beforeunload", this.sendUnregisterCommand);
    if (this.timerSecondsString) {
      this.timerCountdownNumber = JSON.parse(this.timerSecondsString);
    }
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
    if (this.voteSetJson) {
      this.voteSet = JSON.parse(this.voteSetJson);
    }
  },
  beforeUnmount() {
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
    leaveMeeting() {
      window.localStorage.removeItem("memberCookie");
      this.$router.push({ name: "LandingPage" });
    },
    reactOnHostLeave() {
      this.pauseSession = true;
    },
    reactOnHostJoin() {
      this.pauseSession = false;
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
.overlayText {
  font-size: 2em;
  margin: 0.67em 0;
  font-weight: bold;
}
</style>
