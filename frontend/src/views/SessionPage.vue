<template>
  <b-container id="session-page">
    <b-row class="headers">
      <b-col cols="auto" sm="8">
        <h1>
          {{
            planningStart
              ? $t("page.session.during.estimation.title")
              : $t("page.session.before.title")
          }}
        </h1>
      </b-col>
      <b-col cols="auto" >
        <copy-session-id-popup
          v-if="planningStart"
          class="float-end"
          :session-id="session_sessionID"
        />
      </b-col>
      <b-col cols="auto" style="min-width: 200px;">
        <session-close-button
          :is-planning-start="planningStart"
          :user-story-mode="session_userStoryMode"
        />
      </b-col>
    </b-row>

    <div v-if="!planningStart">
      <div style="text-align: center;">
        <b-img :src="require('@/assets/LoadingCat.gif')" class="catGif"/>
      </div>
      <copy-session-id-popup
        :text-before-session-i-d="$t('page.session.before.text.beforeID')"
        :session-id="session_sessionID"
        :text-after-session-i-d="$t('page.session.before.text.afterID')"
        style="text-align: center;"
      />


      <b-row class="d-flex justify-content-center overflow-auto" style="max-height: 500px">
        <kick-user-wrapper
          v-for="member of members"
          :key="member.memberID"
          class="m-4"
          child="RoundedAvatar"
          :member="member"
        />
      </b-row>
      <b-row>
        <b-col class="text-center">
          <session-start-button @clicked="onPlanningStarted" />
        </b-col>
      </b-row>
    </div>

    <div v-else>
      <b-row class="d-flex justify-content-start pb-3">
        <b-col cols="auto" class="mr-auto" style="margin-top: auto; margin-bottom: auto;">
          <b-button
            class="mr-3 optionButton"
            variant="outline-dark"
            @click="sendRestartMessage(); $event.target.blur();"
          >
            <BIconArrowClockwise class="bIcons"></BIconArrowClockwise>
            {{ $t("page.session.during.estimation.buttons.new") }}
          </b-button>
          <b-button class="mr-3 optionButton"
            variant="outline-dark"
            @click="sendVotingFinishedMessage(); $event.target.blur();"
          >
            <BIconBarChartFill class="bIcons"></BIconBarChartFill>
            {{ $t("page.session.during.estimation.buttons.result") }}
          </b-button>
        </b-col>
        <b-col cols="auto">
          <estimate-timer
            :start-timestamp="timerTimestamp"
            :pause-timer="estimateFinished"
            :duration="timerCountdownNumber"
            @timerFinished="sendVotingFinishedMessage"
          />
        </b-col>
      </b-row>

      <h4 v-if="membersPending.length > 0 && !estimateFinished" class="d-inline">
        {{ $t("page.session.during.estimation.message.waitingFor") }}
        {{ membersPending.length }} /
        {{ membersPending.length + membersEstimated.length }}
      </h4>
      <b-row v-if="!estimateFinished" class="my-2 d-flex justify-content-center flex-wrap">
        <kick-user-wrapper
          v-for="member of membersPending"
          :key="member.memberID"
          class="mx-2"
          child="RoundedAvatar"
          :member="member"
        />
      </b-row>
      <hr class="my-5" style="border-color: var(--text-primary-color);"/>
      <h4>
        {{ $t("page.session.during.estimation.message.finished") }}
        {{ membersEstimated.length }} /
        {{ membersPending.length + membersEstimated.length }}
      </h4>
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
    <b-row v-if="session_userStoryMode !== 'NO_US'" class="mt-4">
      <b-col>
        <user-story-sum-component/>
      </b-col>
    </b-row>
    <b-row v-if="session_userStoryMode !== 'NO_US'">
      <b-col v-if="!isMobile" cols="7">
        <div v-if="session_userStoryMode === 'US_JIRA'" class="refreshUserstories">
          <b-button
            class="w-100 mb-3 refreshButton" 
            @click="refreshUserStories(); $event.target.blur();"
          >
            {{ $t("page.session.before.refreshStories") }}
          </b-button>
        </div>
        <user-stories
          :card-set="voteSet"
          :show-estimations="planningStart"
          :initial-stories="userStories"
          :show-edit-buttons="true"
          :select-story="true"
          @userStoriesChanged="onUserStoriesChanged"
          @selectedStory="onSelectedStory($event)"
        />
      </b-col>
      <b-col v-else cols="12">
        <div v-if="session_userStoryMode === 'US_JIRA'" class="refreshUserstories">
          <b-button
            class="w-100 mb-3 refreshButton" 
            @click="refreshUserStories(); $event.target.blur();"
          >
            {{ $t("page.session.before.refreshStories") }}
          </b-button>
        </div>
        <user-stories
          :card-set="voteSet"
          :show-estimations="planningStart"
          :initial-stories="userStories"
          :show-edit-buttons="true"
          :select-story="true"
          @userStoriesChanged="onUserStoriesChanged"
          @selectedStory="onSelectedStory($event)"
        />
      </b-col>
    </b-row>
    <b-row>
      <b-col v-if="!isMobile" cols="10">
        <user-story-descriptions
          :card-set="voteSet"
          :initial-stories="userStories"
          :edit-description="true"
          :index="index"
          @userStoriesChanged="onUserStoriesChanged"
        />
      </b-col>
      <b-col v-else cols="12">
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
    <div class="chatContainer" v-if="!isMobile">
      <div v-if="!chat" class="chatContent">
        <b-button class="chatButton" @click="chat = true">
          <BIconMessenger></BIconMessenger>
        </b-button>
      </div>
      <div v-else class="chatContent">
        <div id="chatRoom"></div>
        <div id="chatting">
            <input v-model="message" id="chatTextArea" placeholder="Type in your Message..." />
            <div id="sendButtonDiv"><b-button id="sendButton" @click="sendMessage"><BIconCursor/></b-button></div>
        </div>
        <b-button class="chatButton" @click="chat = false">
          <BIconArrowDownShort></BIconArrowDownShort>
        </b-button>
      </div>
    </div>
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
import SessionCloseButton from "@/components/actions/SessionCloseButton.vue";
import SessionStartButton from "@/components/actions/SessionStartButton.vue";
import { BIconArrowClockwise, BIconArrowUpShort, BIconBarChartFill, BIconCursor, BIconMessenger } from "bootstrap-vue";

export default Vue.extend({
  name: "SessionPage",
  components: {
    SessionStartButton,
    SessionCloseButton,
    KickUserWrapper,
    UserStorySumComponent,
    UserStories,
    EstimateTimer,
    CopySessionIdPopup,
    UserStoryDescriptions,
    NotifyHostComponent,
    BIconArrowClockwise,
    BIconBarChartFill,
    BIconMessenger,
    BIconCursor
},
  props: {
    adminID: { type: String, required: false },
    sessionID: { type: String, required: false },
    voteSetJson: { type: String, required: false },
    sessionState: { type: String, required: false },
    timerSecondsString: { type: String, required: false },
    startNewSessionOnMountedString: {
      type: String,
      required: false,
      default: "false",
    },
    userStoryMode: { type: String, required: false },
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
      chat: false,
      message: ""
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
    isMobile() {
      return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
        navigator.userAgent
      );
    },
  },
  watch: {
    webSocketIsConnected(isConnected) {
      if (isConnected) {
        console.debug("SessionPage: member connected to websocket");
        setTimeout(() => {
          this.registerAdminPrincipalOnBackend();
          this.subscribeWSMemberUpdated();
          this.requestMemberUpdate();
          this.subscribeOnTimerStart();
          this.subscribeWSNotification();
          if (this.startNewSessionOnMountedString === "true") {
            this.sendRestartMessage();
          }
        }, 50);
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
    membersEstimated() {
      if (this.membersPending.length  === 0 && this.membersEstimated.length > 0) {
        this.estimateFinished = true;
      }
    },
  },
  async created() {
    this.copyPropsToData();
    this.$store.commit("clearStoreWithoutUserStories");
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
    if (this.session_voteSetJson) {
      this.voteSet = JSON.parse(this.session_voteSetJson);
    }
    this.connectToWebSocket();
    if (this.session_sessionState === Constants.memberUpdateCommandStartVoting) {
      this.planningStart = true;
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
      if (this.adminID) {
        this.session_adminID = this.adminID;
        this.session_sessionID = this.sessionID;
        this.session_sessionState = this.sessionState;
        this.session_timerSecondsString = this.timerSecondsString;
        this.session_voteSetJson = this.voteSetJson;
        this.session_userStoryMode = this.userStoryMode;
      }
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
    },
    async onUserStoriesChanged({ us, idx, doRemove }) {
      console.log(`stories: ${us}`);
      console.log(`idx: ${idx}`);
      console.log(`doRemove: ${doRemove}`);
      console.log(`Syncing ${us[idx]}`);
      //Jira sync
      if (this.session_userStoryMode === "US_JIRA") {
        let response;
        if (doRemove) {
          response = await apiService.deleteUserStory(us[idx].id);
          us.splice(idx, 1);
          doRemove = false;
        } else {
          console.log(`ID: ${us[idx].id}`);
          if (us[idx].id === null) {
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
              console.log(`assigned id: ${us[idx].id}`);
            }
          } else {
            response = await apiService.updateUserStory(JSON.stringify(us[idx]));
          }
        }
        if (response.status === 200) {
          this.$toast.success(this.$t("session.notification.messages.issueTrackerSynchronizeSuccess"));
        } else {
          this.$toast.error(this.$t("session.notification.messages.issueTrackerSynchronizeFailed"));
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
    async refreshUserStories() {
      const response = await apiService.getUserStoriesFromProject(this.selectedProject.name);
      this.$store.commit("setUserStories", { stories: response });
      if (this.webSocketIsConnected) {
        const endPoint = `${Constants.webSocketAdminUpdatedUserStoriesRoute}`;
        this.$store.commit("sendViaBackendWS", {
          endPoint,
          data: JSON.stringify(response),
        });
      }
    },
    async onSynchronizeJira({ story, doRemove }) {
      if (this.session_userStoryMode === "US_JIRA") {
        let response;
        if (doRemove) {
          response = await apiService.deleteUserStory(story.id);
        } else {
          console.log(`ID: ${story.id}`);
          if (story.id === null) {
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
          this.$toast.success(this.$t("session.notification.messages.issueTrackerSynchronizeSuccess"));
        } else {
          this.$toast.error(this.$t("session.notification.messages.issueTrackerSynchronizeFailed"));
        }
      }
    },
    onSelectedStory($event) {
      if (this.planningStart) {
        const endPoint = Constants.webSocketAdminSelectedUserStoryRoute;
        this.$store.commit("sendViaBackendWS", { endPoint, data: $event });
      }
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
    subscribeOnChatMessage() {
      this.$store.commit("subscribeOnBackendWSChatMessage");
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
    sendVotingFinishedMessage() {
      if (!this.estimateFinished) {
        const endPoint = Constants.webSocketVotingFinishedRoute;
        this.$store.commit("sendViaBackendWS", { endPoint });
        this.estimateFinished = true;
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
    onPlanningStarted() {
      this.planningStart = true;
    },
    sendMessage() {
      const endPoint = Constants.webSocketAdminSendMessageRoute;
      this.$store.commit("sendViaBackendWS", { endPoint, data: this.message });
      this.message = "";
    }
  },
});
</script>

<style scoped>

.headers {
  display: flex;
  align-items: center;
  min-height: 20vh;
}

.bIcons {
  height: 40px;
  width: 40px;
}

.optionButton{
  background-color: var(--textAreaColour);
  color: var(--text-primary-color);
  border-color: black;
  border-radius: var(--buttonShape);
  display: inline-flex;
  align-items: center;
}

.optionButton:hover{
  background-color: var(--textAreaColourHovered);
  color: var(--text-primary-color);
}

.optionButton:focus{
  background-color: var(--textAreaColourHovered) !important;
  color: var(--text-primary-color)  !important;
}

.refreshButton {
  border-radius: var(--element-size);
  color: var(--text-primary-color);
  background-color: var(--joinButton);
}

.refreshButton:hover {
  color: var(--text-primary-color);
  background-color: var(--joinButtonHovered);
}

.refreshButton:focus {
  background-color: var(--joinButtonHovered) !important;
  color: var(--text-primary-color) !important;
}

.catGif {
  width: 240px; 
  height: 180px;
}

.chatButton {
  background-color: var(--textAreaColour);
  color: var(--text-primary-color);
  border-color: black;
  border-radius: var(--buttonShape);
  bottom: 5%;
  right: 1%;
  position: absolute;
}

.chatButton:hover{
  background-color: var(--textAreaColourHovered);
  color: var(--text-primary-color);
}

.chatButton:focus{
  background-color: var(--textAreaColourHovered) !important;
  color: var(--text-primary-color)  !important;
}

.chatContainer {
  position: fixed;
  bottom: 0%;
  right: 0%;

  height: 25%;
  width: 20%;
}

.chatContainerMobile {
  position: relative;
  bottom: 0%;
  right: 1%;
  margin-top: 4%;
  overflow: hidden;
}

#chatting {
  position: absolute;
  right: 2.5%;
  bottom: 20%;
  width: 94.5%;
  margin-bottom: 2%;
  align-items: center;
  justify-content: center;
  display: flex;
}

#sendButtonDiv {
  display:inline-block;
  margin-left: 5%;
}

#sendButton {
  background:none;
  border:none;
  color: var(--text-primary-color);
  padding: 0;
  height: 110%;
  width: 110%;
}

#sendButton:focus {
  background-color: transparent !important;
  border-color: inherit;
  -webkit-box-shadow: none;
  box-shadow: none;
}

#chatTextArea {
  resize: none;
  background-color: var(--textAreaColour);
  color: var(--text-primary-color);
  width: 100%;
  overflow:scroll;
}

#chatTextArea::placeholder {
  color: var(--text-primary-color);
}

#chatRoom {
  position: absolute;
  width: 95%;
  height: 60%;
  bottom: 37%;
  left: 2.5%;
  margin-bottom: 15%;

  background-color: var(--textAreaColour);
  border-color: black;
  border-style: solid;

  overflow: scroll;
}
</style>
