<template>
  <b-container id="member-vote-page">
    <b-overlay :show="pauseSession">
      <template #overlay>
        <b-spinner class="me-2" />
        <span class="overlayText">
          {{ t("page.vote.hostLeft") }}
        </span>
      </template>

      <b-row class="headers">
        <b-col>
          <h1>{{ t("page.vote.title") }}</h1>
        </b-col>

        <b-col cols="auto">
          <session-leave-button />
          <estimate-timer
            v-if="timerTimestamp"
            class="mt-3"
            :start-timestamp="timerTimestamp"
            :pause-timer="estimateFinished || pauseSession"
            :duration="timerCountdownNumber"
            :member="memberID"
            :voting-started="isStartVoting"
          />
        </b-col>
      </b-row>
      <b-row>
        <b-col cols="auto" class="memberIcon">
          <rounded-avatar :member="getMember" :admin="false" />
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
      <b-row v-if="isStartVoting">
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
              :hex-color="hexColor ?? ''"
              :dragged="voteOption === draggedVote"
              :is-mobile="true"
              :disabled="pauseSession"
              @sentVote="onSendVote"
            />
          </flicking>
        </div>
        <b-row v-else class="centerCards d-flex justify-content-between flex-wrap text-center">
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
                :hex-color="hexColor ?? ''"
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
        <h3 id="header">
          {{ t("page.vote.waiting") }}
          <sub>
            <b-icon-three-dots animation="fade" font-scale="1" />
          </sub>
        </h3>
      </b-row>
      <b-row
        v-if="votingFinished && isAdminHighlighted()"
        class="my-1 d-flex justify-content-center flex-wrap overflow-auto"
        style="max-height: 500px"
      >
        <div v-if="hostEstimation !== undefined">
          <div v-if="hostVoting && hostEstimation.hostEstimation !== null">
            <session-admin-card
              v-if="hostEstimation.hostEstimation !== null"
              :current-estimation="hostEstimation.hostEstimation"
              :estimate-finished="votingFinished"
              :highlight="isAdminHighlighted()"
            />
          </div>
        </div>
        <session-member-card
          v-for="member of members"
          :key="member.memberID"
          :member="member"
          :props="{
            estimateFinished: votingFinished,
            highlight:
              highlightedMembers.includes(member.memberID) || highlightedMembers.length === 0,
          }"
        />
      </b-row>
      <b-row
        v-if="votingFinished && isAdminHighlighted() === false"
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
              highlightedMembers.includes(member.memberID) || highlightedMembers.length === 0,
          }"
        />
        <div v-if="hostEstimation !== undefined">
          <div v-if="hostVoting && hostEstimation.hostEstimation !== null">
            <session-admin-card
              v-if="hostEstimation.hostEstimation !== null"
              :current-estimation="hostEstimation.hostEstimation"
              :estimate-finished="votingFinished"
              :highlight="isAdminHighlighted()"
            />
          </div>
        </div>
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
              :host-selected-story-index="hostSelectedStoryIndex"
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
            :host-selected-story-index="hostSelectedStoryIndex"
            @selectedStory="onSelectedStory($event)"
          />
        </div>
      </b-col>
      <notify-member-component @hostLeft="reactOnHostLeave" @hostJoined="reactOnHostJoin" />
    </b-overlay>
  </b-container>
</template>

<script lang="ts">
// eslint-disable-next-line
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
import SessionAdminCard from "@/components/SessionAdminCard.vue";
import { defineComponent } from "vue";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";
import AdminVote from "@/model/AdminVote";

export default defineComponent({
  name: "MemberVotePage",
  components: {
    SessionLeaveButton,
    RoundedAvatar,
    MemberVoteCard,
    EstimateTimer,
    SessionMemberCard,
    SessionAdminCard,
    NotifyMemberComponent,
    UserStories,
    UserStoryDescriptions,
    MobileStoryList,
    MobileStoryTitle,
    UserStorySumComponent,
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    return { store, toast, t };
  },
  data() {
    return {
      index: 0,
      hostSelectedStoryIndex: undefined,
      draggedVote: null,
      voteSet: [] as string[],
      timerCountdownNumber: 0,
      triggerTimer: 0,
      estimateFinished: false,
      pauseSession: false,
      memberID: history.state.memberID,
      name: history.state.name,
      hexColor: history.state.hexColor,
      avatarAnimalAssetName: history.state.avatarAnimalAssetName,
      voteSetJson: history.state.voteSetJson,
      timerSecondsString: history.state.timerSecondsString,
      userStoryMode: history.state.userStoryMode,
      safedHostEstimation: undefined as string | undefined,
    };
  },
  computed: {
    isMobile() {
      return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
        navigator.userAgent
      );
    },
    userStories() {
      return this.store.userStories;
    },
    memberUpdates() {
      return this.store.memberUpdates;
    },
    isStartVoting(): boolean {
      return this.memberUpdates.at(-1) === Constants.memberUpdateCommandStartVoting;
    },
    isAutoRevealActive(): boolean {
      return this.store.autoReveal;
    },
    votingFinished(): boolean {
      return this.memberUpdates.at(-1) === Constants.memberUpdateCommandVotingFinished;
    },
    members() {
      return this.store.members;
    },
    membersEstimated(): Member[] {
      return this.members.filter((member: Member) => member.currentEstimation !== null);
    },
    highlightedMembers() {
      return this.store.highlightedMembers;
    },
    timerTimestamp() {
      return this.store.timerTimestamp ? this.store.timerTimestamp : "";
    },
    notifications() {
      return this.store.notifications;
    },
    hostVoting(): boolean {
      return this.store.hostVoting;
    },
    hostEstimation() {
      return this.store.hostEstimation;
    },
    getMember() {
      return {
        memberID: "",
        name: this.name,
        hexColor: this.hexColor,
        avatarAnimalAssetName: this.avatarAnimalAssetName,
        currentEstimation: "",
      } as Member;
    },
    selectedUserStoryIndex() {
      return this.store.selectedUserStoryIndex;
    },
  },
  watch: {
    memberUpdates(updates) {
      if (updates.at(-1) === Constants.memberUpdateCommandStartVoting) {
        this.draggedVote = null;
        this.estimateFinished = false;
        this.safedHostEstimation = undefined;
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
      if (this.hostVoting) {
        this.safedHostEstimation = this.hostEstimation?.hostEstimation;
      }
    },
    notifications(notifications) {
      const lastNotification = this.notifications.at(-1);
      if (
        lastNotification &&
        lastNotification?.type === "MEMBER_LEFT" &&
        lastNotification?.payload.memberID === this.memberID
      ) {
        this.toast.error(this.t("session.notification.messages.memberRemoved"));
        this.leaveMeeting();
      }
    },
    selectedUserStoryIndex(index) {
      this.hostSelectedStoryIndex = index;
    },
  },
  created() {
    if (this.timerSecondsString !== undefined) {
      this.timerCountdownNumber = Number.parseInt(this.timerSecondsString);
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
    this.voteSet = JSON.parse(this.voteSetJson ?? "{}");
  },
  methods: {
    isAdminHighlighted() {
      if (this.highlightedMembers.length === 0) {
        return true;
      }
      let highlightedMap = new Map<string, boolean>();
      this.highlightedMembers.forEach((highlightedMemberID) => {
        let isMemberID = false;
        this.members.forEach((member) => {
          if (member.memberID === highlightedMemberID) {
            isMemberID = true;
          }
        });
        highlightedMap.set(highlightedMemberID, isMemberID);
      });
      for (let value of highlightedMap.values()) {
        if (value === false) {
          return true;
        }
      }
      return false;
    },
    onSelectedStory($event) {
      this.index = $event;
    },
    onSendVote({ vote }) {
      this.draggedVote = vote;
      const endPoint = `${Constants.webSocketVoteRoute}`;
      this.store.sendViaBackendWS(
        endPoint,
        JSON.stringify({
          vote: vote,
          autoReveal: this.isAutoRevealActive,
        })
      );
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

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
#header {
  color: var(--text-primary-color);
}

#flicking {
  /* overflow:visible;  Add when fix is clear how to stay responsiv*/
  width: 100%;
}

.overlayText {
  font-size: 2em;
  margin: 0.67em 0;
  font-weight: bold;
}

.headers {
  display: flex;
  align-items: center;
  min-height: 10vh;
}

.memberIcon {
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 5%;
}

.centerCards {
  margin-left: auto;
  margin-right: auto;
}
</style>
