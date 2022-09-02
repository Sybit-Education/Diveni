<template>
  <b-container>
    <b-row class="mb-3">
      <b-col>
        <h1>
          {{
            planningStart
              ? $t("page.session.during.estimation.title")
              : $t("page.session.before.title")
          }}
        </h1>
      </b-col>
      <b-col v-if="planningStart" align-self="center">
        <copy-session-id-popup class="float-end" :session-id="session_sessionID" />
      </b-col>
    </b-row>
    <div v-if="!planningStart">
      <b-row class="align-items-center">
        <copy-session-id-popup
          :text-before-session-i-d="$t('page.session.before.text.beforeID')"
          :session-id="session_sessionID"
          :text-after-session-i-d="$t('page.session.before.text.afterID')"
        />
      </b-row>
      <b-row class="mt-5">
        <h4 class="text-center">
          {{ $t("page.session.before.text.waiting") }}
        </h4>
        <b-icon-three-dots animation="fade" font-scale="3" />
      </b-row>
      <b-row class="d-flex justify-content-center overflow-auto" style="max-height: 500px">
        <kick-user-wrapper
          v-for="member of members"
          :key="member.memberID"
          class="m-4"
          child="SessionMemberCircle"
          :member="member"
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
              {{ $t("page.session.close.description1") }}
            </p>
            <p v-if="session_userStoryMode !== 'NO_US'">
              {{ $t("page.session.close.description2") }}
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
        <kick-user-wrapper
          v-for="member of membersPending"
          :key="member.memberID"
          class="mx-2"
          child="RoundedAvatar"
          :member="member"
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
        <kick-user-wrapper
          v-for="member of estimateFinished ? members : membersEstimated"
          :key="member.memberID"
          child="SessionMemberCard"
          :member="member"
          :props="{
            estimateFinished: estimateFinished,
            highlight:
              highlightedMembers.includes(member.memberID) || highlightedMembers.length === 0,
          }"
        />
      </b-row>
    </div>
    <b-row v-if="session_userStoryMode !== 'NO_US'" class="mt-5">
      <b-col md="6">
        <UserStorySumComponent class="ms-4" />
      </b-col>
    </b-row>
    <b-row v-if="session_userStoryMode !== 'NO_US'">
      <b-col>
        <div class="overflow-auto" style="max-height: 700px">
          <user-stories
            :card-set="voteSet"
            :show-estimations="planningStart"
            :initial-stories="userStories"
            :show-edit-buttons="true"
            :select-story="true"
            @userStoriesChanged="onUserStoriesChanged"
            @selectedStory="onSelectedStory($event)"
          />
        </div>
      </b-col>
      <b-col>
        <user-story-descriptions
          :card-set="voteSet"
          :initial-stories="userStories"
          :edit-description="true"
          :index="index"
          @userStoriesChanged="onUserStoriesChanged"
        />
      </b-col>
    </b-row>
    <notify-host-component />
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import Constants from "../constants";
import Member from "../model/Member";
import UserStories from "../components/UserStories.vue";
import EstimateTimer from "../components/EstimateTimer.vue";
import CopySessionIdPopup from "../components/CopySessionIdPopup.vue";
import UserStoryDescriptions from "../components/UserStoryDescriptions.vue";
import confetti from "canvas-confetti";
import NotifyHostComponent from "../components/NotifyHostComponent.vue";
import apiService from "@/services/api.service";
import UserStorySumComponent from "@/components/UserStorySum.vue";
import Project from "../model/Project";
import KickUserWrapper from "@/components/KickUserWrapper.vue";

export default Vue.extend({
  name: "SessionPage",
  components: {
    KickUserWrapper,
    UserStorySumComponent,
    UserStories,
    EstimateTimer,
    CopySessionIdPopup,
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
    userStoryMode: { type: String, required: true },
  },
  data() {
    return {
      //props copy
      session_adminID: "",
      session_sessionID: "",
      session_voteSetJson: "",
      session_sessionState: "",
      session_timerSecondsString: "",
      session_userStoryMode: "",
      //data
      index: 0,
      stageLabelReady: "Ready",
      stageLabelWaiting: "Waiting room",
      planningStart: false,
      voteSet: [] as string[],
      timerCountdownNumber: 0,
      startTimerOnComponentCreation: true,
      estimateFinished: false,
      session: {},
    };
  },
  computed: {
    selectedProject(): Project {
      return this.$store.state.selectedProject;
    },
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
        setTimeout(() => {
          if (this.members.length === 0) {
            this.requestMemberUpdate();
          }
        }, 300);
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
  async created() {
    this.copyPropsToData();
    if (!this.session_sessionID || !this.session_adminID) {
      //check for cookie
      await this.checkAdminCookie();
      this.assignSessionToData(this.session);
      if (this.session_sessionID.length === 0) {
        this.goToLandingPage();
      } else {
        this.handleReload();
      }
    }
    this.timerCountdownNumber = parseInt(this.session_timerSecondsString, 10);
    window.addEventListener("beforeunload", this.sendUnregisterCommand);
  },
  mounted() {
    this.voteSet = JSON.parse(this.session_voteSetJson);
    this.connectToWebSocket();
    if (this.session_sessionState === Constants.memberUpdateCommandStartVoting) {
      this.planningStart = true;
      this.sendRestartMessage();
    } else if (this.session_sessionState === Constants.memberUpdateCommandVotingFinished) {
      this.planningStart = true;
      this.estimateFinished = true;
    }
  },
  destroyed() {
    window.removeEventListener("beforeunload", this.sendUnregisterCommand);
  },
  methods: {
    async checkAdminCookie() {
      console.log("checking admin cookie");
      const cookie = window.localStorage.getItem("adminCookie");
      if (cookie !== null) {
        console.log(`Found admin cookie: '${cookie}'`);
        const url = Constants.backendURL + Constants.createSessionRoute;
        try {
          const session = (
            await this.axios.get(url, {
              params: {
                adminCookie: cookie,
              },
            })
          ).data as {
            sessionID: string;
            adminID: string;
            sessionConfig: {
              set: Array<string>;
              timerSeconds: number;
              userStories: Array<{
                title: string;
                description: string;
                estimation: string | null;
                isActive: false;
              }>;
              userStoryMode: string;
            };
            sessionState: string;
          };
          this.session = session;
        } catch (e) {
          console.clear();
          console.log(`got error: ${e}`);
          window.localStorage.removeItem("adminCookie");
        }
      }
    },
    copyPropsToData() {
      this.session_adminID = this.adminID;
      this.session_sessionID = this.sessionID;
      this.session_sessionState = this.sessionState;
      this.session_timerSecondsString = this.timerSecondsString;
      this.session_voteSetJson = this.voteSetJson;
      this.session_userStoryMode = this.userStoryMode;
    },
    assignSessionToData(session) {
      if (Object.keys(session).length !== 0) {
        this.session_adminID = session.adminID;
        this.session_sessionID = session.sessionID;
        this.session_sessionState = session.sessionState;
        this.session_timerSecondsString = session.sessionConfig.timerSeconds.toString();
        this.session_voteSetJson = JSON.stringify(session.sessionConfig.set);
        this.session_userStoryMode = session.sessionConfig.userStoryMode;
        this.$store.commit("setUserStories", {
          stories: session.sessionConfig.userStories,
        });
        this.voteSet = JSON.parse(this.session_voteSetJson);
      }
    },
    handleReload() {
      if (
        this.session_sessionState === Constants.memberUpdateCommandStartVoting ||
        this.session_sessionState === Constants.memberUpdateCommandVotingFinished
      ) {
        this.planningStart = true;
      }
      if (this.session_sessionState === Constants.memberUpdateCommandVotingFinished) {
        this.estimateFinished = true;
      }
      this.timerCountdownNumber = parseInt(this.session_timerSecondsString, 10);
      //reconnect and reload member
      this.connectToWebSocket();
      this.requestMemberUpdate();
    },
    async onUserStoriesChanged({ us, idx, doRemove }) {
      console.log(`stories: ${us}`);
      console.log(`idx: ${idx}`);
      console.log(`doRemove: ${doRemove}`);
      console.log(`Syncing ${us[idx]}`);
      // Jira sync
      if (this.session_userStoryMode === "US_JIRA") {
        let response;
        if (doRemove) {
          response = await apiService.deleteUserStory(us[idx].jiraId);
          us.splice(idx, 1);
          doRemove = false;
        } else {
          console.log(`JIRA ID: ${us[idx].jiraID}`);
          if (us[idx].jiraId === null) {
            response = await apiService.createUserStory(
              JSON.stringify(us[idx]),
              this.selectedProject.id
            );
            if (response.status === 200) {
              us = this.userStories.map((s) =>
                s.title === us[idx].title && s.description === us[idx].description
                  ? { ...s, jiraId: response.data }
                  : s
              );
              console.log(`assigned id: ${us[idx].jiraId}`);
            }
          } else {
            response = await apiService.updateUserStory(JSON.stringify(us[idx]));
          }
        }
        if (response.status === 200) {
          this.$toast.success(this.$t("session.notification.messages.jiraSynchronizeSuccess"));
        } else {
          this.$toast.error(this.$t("session.notification.messages.jiraSynchronizeFailed"));
        }
      }
      // WS send
      if (doRemove) {
        us.splice(idx, 1);
      }
      this.$store.commit("setUserStories", { stories: us });
      if (this.webSocketIsConnected) {
        const endPoint = `${Constants.webSocketAdminUpdatedUserStoriesRoute}`;
        this.$store.commit("sendViaBackendWS", {
          endPoint,
          data: JSON.stringify(us),
        });
      }
    },
    async onSynchronizeJira({ story, doRemove }) {
      if (this.session_userStoryMode === "US_JIRA") {
        let response;
        if (doRemove) {
          response = await apiService.deleteUserStory(story.jiraId);
        } else {
          console.log(`JIRA ID: ${story.jiraID}`);
          if (story.jiraId === null) {
            response = await apiService.createUserStory(
              JSON.stringify(story),
              this.selectedProject.id
            );
            if (response.status === 200) {
              const updatedStories = this.userStories.map(
                (s) => s.title === story.title && s.description === story.description
              );
              this.$store.commit("setUserStories", updatedStories);
            }
          } else {
            response = await apiService.updateUserStory(JSON.stringify(story));
          }
        }
        if (response.status === 200) {
          this.$toast.success(this.$t("session.notification.messages.jiraSynchronizeSuccess"));
        } else {
          this.$toast.error(this.$t("session.notification.messages.jiraSynchronizeFailed"));
        }
      }
    },
    onSelectedStory($event) {
      this.index = $event;
    },
    connectToWebSocket() {
      const url = `${Constants.backendURL}/connect?sessionID=${this.session_sessionID}&adminID=${this.session_adminID}`;
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
    closeSession() {
      this.sendCloseSessionCommand();
      window.localStorage.removeItem("adminCookie");
      if (this.session_userStoryMode !== "NO_US") {
        this.$router.push({ name: "ResultPage" });
      } else {
        this.$store.commit("clearStore");
        this.$router.push({ name: "LandingPage" });
      }
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
