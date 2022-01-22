<template>
  <b-container>
    <b-row class="mt-5 mb-3">
      <b-col
        ><h1>
          {{
            planningStart
              ? $t("page.session.during.estimation.title")
              : $t("page.session.before.title")
          }}
        </h1></b-col
      >
      <b-col v-if="planningStart" align-self="center">
        <copy-session-id-popup class="float-end" :session-id="sessionID" />
      </b-col>
    </b-row>
    <div v-if="!planningStart">
      <b-row class="align-items-center">
        <copy-session-id-popup
          :text-before-session-i-d="$t('page.session.before.text.beforeID')"
          :session-id="sessionID"
          :text-after-session-i-d="$t('page.session.before.text.afterID')"
        />
      </b-row>
      <b-row class="mt-5">
        <h4 class="text-center">
          {{ $t("page.session.before.text.waiting") }}
        </h4>
        <b-icon-three-dots animation="fade" class="" font-scale="3" />
      </b-row>
      <b-row class="d-flex justify-content-center overflow-auto" style="max-height: 500px">
        <SessionMemberCircle
          v-for="member of members"
          :key="member.memberID"
          class="m-4"
          :color="member.hexColor"
          :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
          :alt-attribute="member.avatarAnimal"
          :name="member.name"
        />
      </b-row>
      <b-row>
        <b-col class="text-center">
          <b-button
            variant="success"
            :disabled="!members || members.length < 1"
            @click="sendStartEstimationMessages"
          >
            {{ $t("page.session.before.button") }}
          </b-button>
        </b-col>
      </b-row>
    </div>
    <div v-else>
      <b-row class="d-flex justify-content-start pb-3">
        <b-col>
          <b-button variant="outline-dark" @click="sendRestartMessage">
            <b-icon-arrow-clockwise />
            {{ $t("page.session.during.estimation.buttons.new") }}
          </b-button>
          <b-button variant="outline-dark" class="mx-1" @click="sendVotingFinishedMessage">
            <b-icon-bar-chart />
            {{ $t("page.session.during.estimation.buttons.result") }}
          </b-button>
          <b-button v-b-modal.close-session-modal variant="danger">
            <b-icon-x />
            {{ $t("page.session.during.estimation.buttons.finish") }}
          </b-button>
          <b-modal
            id="close-session-modal"
            :title="$t('page.session.close.popup')"
            :cancel-title="$t('page.session.close.button.cancel')"
            :ok-title="$t('page.session.close.button.ok')"
            @ok="closeSession"
          >
            <p class="my-4">
              {{ $t("page.session.close.description") }}
            </p>
          </b-modal>
        </b-col>
        <b-col>
          <estimate-timer
            :start-timestamp="timerTimestamp"
            :pause-timer="estimateFinished"
            :duration="timerCountdownNumber"
            @timerFinished="sendVotingFinishedMessage"
          />
        </b-col>
      </b-row>
      <b-row v-if="membersPending.length > 0 && !estimateFinished">
        <h4 class="d-inline">
          {{ $t("page.session.during.estimation.message.waitingFor") }}
          {{ membersPending.length }} /
          {{ membersPending.length + membersEstimated.length }}
        </h4>
      </b-row>
      <b-row v-if="!estimateFinished" class="my-1 d-flex justify-content-center flex-wrap">
        <rounded-avatar
          v-for="member of membersPending"
          :key="member.memberID"
          class="mx-2"
          :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
          :color="member.hexColor"
          :show-name="true"
          :name="member.name"
        />
      </b-row>
      <hr />
      <b-row>
        <h4 class="d-inline">
          {{ $t("page.session.during.estimation.message.finished") }}
          {{ membersEstimated.length }} /
          {{ membersPending.length + membersEstimated.length }}
        </h4>
      </b-row>
      <b-row
        class="my-1 d-flex justify-content-center flex-wrap overflow-auto"
        style="max-height: 500px"
      >
        <SessionMemberCard
          v-for="member of estimateFinished ? members : membersEstimated"
          :key="member.memberID"
          :color="member.hexColor"
          :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
          :name="member.name"
          :estimation="member.currentEstimation"
          :estimate-finished="estimateFinished"
          :highlight="
            highlightedMembers.includes(member.memberID) || highlightedMembers.length === 0
          "
        />
      </b-row>
    </div>
    <b-row>
      <b-col class="mt-2">
        <div class="overflow-auto" style="max-height: 700px">
          <user-stories-sidebar
            :card-set="voteSet"
            :show-estimations="planningStart"
            :initial-stories="userStories"
            :show-edit-buttons="true"
            :select-story="true"
            @userStoriesChanged="onUserStoriesChanged($event)"
            @selectedStory="onSelectedStory($event)"
          />
        </div>
      </b-col>

      <b-col class="mt-2">
        <user-story-descriptions
          :card-set="voteSet"
          :initial-stories="userStories"
          :edit-description="true"
          :index="index"
          @userStoriesChanged="onUserStoriesChanged($event)"
        />
      </b-col>
    </b-row>
    <notify-host-component />
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import Constants from "../constants";
import SessionMemberCircle from "../components/SessionMemberCircle.vue";
import Member from "../model/Member";
import SessionMemberCard from "../components/SessionMemberCard.vue";
import UserStoriesSidebar from "../components/UserStories.vue";
import EstimateTimer from "../components/EstimateTimer.vue";
import CopySessionIdPopup from "../components/CopySessionIdPopup.vue";
import RoundedAvatar from "../components/RoundedAvatar.vue";
import UserStoryDescriptions from "../components/UserStoryDescriptions.vue";
import confetti from "canvas-confetti";
import NotifyHostComponent from "../components/NotifyHostComponent.vue";

export default Vue.extend({
  name: "SessionPage",
  components: {
    SessionMemberCircle,
    SessionMemberCard,
    UserStoriesSidebar,
    EstimateTimer,
    CopySessionIdPopup,
    RoundedAvatar,
    UserStoryDescriptions,
    NotifyHostComponent,
  },
  props: {
    adminID: { type: String, required: true },
    sessionID: { type: String, required: true },
    voteSetJson: { type: String, required: true },
    sessionState: { type: String, required: true },
    timerSecondsString: { type: String, required: true },
    startNewSessionOnMountedString: {
      type: String,
      required: false,
      default: "false",
    },
  },
  data() {
    return {
      index: null,
      stageLabelReady: "Ready",
      stageLabelWaiting: "Waiting room",
      planningStart: false,
      voteSet: [] as string[],
      timerCountdownNumber: 0,
      triggerTimer: 0,
      startTimerOnComponentCreation: true,
      estimateFinished: false,
    };
  },
  computed: {
    userStories() {
      return this.$store.state.userStories;
    },
    members() {
      return this.$store.state.members;
    },
    webSocketIsConnected() {
      return this.$store.state.webSocketConnected;
    },
    highlightedMembers() {
      return this.$store.state.highlightedMembers;
    },
    membersPending(): Member[] {
      return this.members.filter((member: Member) => member.currentEstimation === null);
    },
    membersEstimated(): Member[] {
      return this.members.filter((member: Member) => member.currentEstimation !== null);
    },
    timerTimestamp() {
      return this.$store.state.timerTimestamp ? this.$store.state.timerTimestamp : "";
    },
  },
  watch: {
    webSocketIsConnected(isConnected) {
      if (isConnected) {
        console.debug("SessionPage: member connected to websocket");
        this.registerAdminPrincipalOnBackend();
        this.subscribeWSMemberUpdated();
        this.requestMemberUpdate();
        this.subscribeOnTimerStart();
        this.subscribeWSNotification();
        if (this.startNewSessionOnMountedString === "true") {
          this.sendRestartMessage();
        }
      }
    },
    highlightedMembers(highlights) {
      if (this.estimateFinished && highlights.length === 0) {
        confetti({
          particleCount: 100,
          startVelocity: 50,
          spread: 100,
        });
      }
    },
  },
  created() {
    if (!this.sessionID || !this.adminID) {
      this.goToLandingPage();
    }
    this.timerCountdownNumber = parseInt(this.timerSecondsString, 10);
    window.addEventListener("beforeunload", this.sendUnregisterCommand);
  },
  mounted() {
    this.voteSet = JSON.parse(this.voteSetJson);
    this.connectToWebSocket();
    if (this.sessionState === Constants.memberUpdateCommandStartVoting) {
      this.planningStart = true;
      if (this.planningStart) {
        this.sendRestartMessage();
      }
    }
  },
  destroyed() {
    window.removeEventListener("beforeunload", this.sendUnregisterCommand);
  },
  methods: {
    onUserStoriesChanged($event) {
      this.$store.commit("setUserStories", { stories: $event });
      if (this.webSocketIsConnected) {
        const endPoint = `${Constants.webSocketAdminUpdatedUserStoriesRoute}`;
        this.$store.commit("sendViaBackendWS", {
          endPoint,
          data: JSON.stringify($event),
        });
      }
    },
    onSelectedStory($event) {
      this.index = $event;
    },
    connectToWebSocket() {
      const url = `${Constants.backendURL}/connect?sessionID=${this.sessionID}&adminID=${this.adminID}`;
      this.$store.commit("connectToBackendWS", url);
    },
    registerAdminPrincipalOnBackend() {
      const endPoint = Constants.webSocketRegisterAdminUserRoute;
      this.$store.commit("sendViaBackendWS", { endPoint });
    },
    subscribeWSMemberUpdated() {
      this.$store.commit("subscribeOnBackendWSAdminUpdate");
    },
    subscribeOnTimerStart() {
      this.$store.commit("subscribeOnBackendWSTimerStart");
    },
    requestMemberUpdate() {
      const endPoint = Constants.webSocketGetMemberUpdateRoute;
      this.$store.commit("sendViaBackendWS", { endPoint });
    },
    subscribeWSNotification() {
      this.$store.commit("subscribeOnBackendWSNotify");
    },
    sendUnregisterCommand() {
      const endPoint = `${Constants.webSocketUnregisterRoute}`;
      this.$store.commit("sendViaBackendWS", { endPoint, data: null });
      this.$store.commit("clearStore");
    },
    sendCloseSessionCommand() {
      const endPoint = `${Constants.webSocketCloseSessionRoute}`;
      this.$store.commit("sendViaBackendWS", { endPoint });
    },
    sendStartEstimationMessages() {
      const endPoint = Constants.webSocketStartPlanningRoute;
      this.$store.commit("sendViaBackendWS", { endPoint });
      this.planningStart = true;
    },
    sendVotingFinishedMessage() {
      if (!this.estimateFinished) {
        const endPoint = Constants.webSocketVotingFinishedRoute;
        this.$store.commit("sendViaBackendWS", { endPoint });
        this.estimateFinished = true;
      }
    },
    backendAnimalToAssetName(animal: string) {
      return Constants.avatarAnimalToAssetName(animal);
    },
    closeSession() {
      this.sendCloseSessionCommand();
      window.localStorage.removeItem("adminCookie");
      this.$router.push({ name: "ResultPage" });
    },
    sendRestartMessage() {
      this.estimateFinished = false;
      const endPoint = Constants.webSocketRestartPlanningRoute;
      this.$store.commit("sendViaBackendWS", { endPoint });
    },
    goToLandingPage() {
      this.$router.push({ name: "LandingPage" });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped></style>
