<template>
  <b-container id="session-page">
    <b-row class="headers">
      <b-col cols="auto" sm="8">
        <h1>
          {{
            planningStart
              ? t("page.session.during.estimation.title")
              : t("page.session.before.title")
          }}
        </h1>
      </b-col>
      <b-col>
        <b-button
          v-if="!autoReveal && !planningStart"
          class="mr-3 autoRevealButtons optionButton"
          variant="outline-dark"
          @click="
            autoReveal = true;
            $event.target.blur();
          "
        >
          <b-icon-eye-slash-fill class="bIcons" />
          {{ t("page.session.during.estimation.buttons.autoRevealOff") }}
        </b-button>
        <b-button
          v-if="autoReveal && !planningStart"
          class="mr-3 autoRevealButtons optionButton"
          variant="outline-dark"
          @click="
            autoReveal = false;
            $event.target.blur();
          "
        >
          <b-icon-eye-fill class="bIcons" />
          {{ t("page.session.during.estimation.buttons.autoRevealOn") }}
        </b-button>
      </b-col>
      <b-col cols="auto" class="mr-auto">
        <copy-session-id-popup v-if="planningStart" class="float-end" :session-id="sessionID" />
      </b-col>
      <b-col cols="auto">
        <session-close-button :is-planning-start="planningStart" :user-story-mode="userStoryMode" />
      </b-col>
    </b-row>

    <div v-if="!planningStart">
      <div id="catGifDiv">
        <b-img :src="require('@/assets/LoadingCat.gif')" class="catGif" />
      </div>
      <copy-session-id-popup
        :text-before-session-i-d="t('page.session.before.text.beforeID')"
        :session-id="sessionID"
        :text-after-session-i-d="t('page.session.before.text.afterID')"
        class="copy-popup"
      />

      <b-row
        class="d-flex justify-content-center overflow-auto kick-user"
        :class="isMobile ? 'avatar-maxHeight' : ''"
      >
        <kick-user-wrapper
          v-for="member of members"
          :key="member.memberID"
          :class="isMobile ? 'm-4' : 'spaceBetweenAvatar'"
          child="RoundedAvatar"
          :member="member"
        />
      </b-row>
      <b-row>
        <b-col class="text-center">
          <session-start-button
            :host-voting="hostVoting"
            :auto-reveal="autoReveal"
            @clicked="onPlanningStarted"
          />
        </b-col>
      </b-row>
    </div>
    <div v-else>
      <b-row class="d-flex justify-content-start pb-3">
        <b-col cols="auto" class="mr-auto optionButtonCol">
          <b-button
            class="mr-3 optionButton"
            variant="outline-dark"
            @click="
              sendRestartMessage();
              $event.target.blur();
            "
          >
            <BIconArrowClockwise class="bIcons"></BIconArrowClockwise>
            {{ t("page.session.during.estimation.buttons.new") }}
          </b-button>
          <b-button
            class="mr-3 optionButton"
            variant="outline-dark"
            @click="
              sendVotingFinishedMessage();
              $event.target.blur();
            "
          >
            <BIconBarChartFill class="bIcons"></BIconBarChartFill>
            {{ t("page.session.during.estimation.buttons.result") }}
          </b-button>
          <b-button
            v-if="!autoReveal"
            class="mr-3 optionButton"
            variant="outline-dark"
            :disabled="planningStart && !estimateFinished"
            @click="
              autoReveal = true;
              $event.target.blur();
            "
          >
            <b-icon-eye-slash-fill class="bIcons" />
            {{ t("page.session.during.estimation.buttons.autoRevealOff") }}
          </b-button>
          <b-button
            v-if="autoReveal"
            class="mr-3 optionButton"
            variant="outline-dark"
            :disabled="planningStart && !estimateFinished"
            @click="
              autoReveal = false;
              $event.target.blur();
            "
          >
            <b-icon-eye-fill class="bIcons" />
            {{ t("page.session.during.estimation.buttons.autoRevealOn") }}
          </b-button>
        </b-col>
        <b-col cols="auto">
          <estimate-timer
            :start-timestamp="timerTimestamp"
            :pause-timer="estimateFinished"
            :duration="timerCountdownNumber"
            :voting-started="planningStart"
            @timerFinished="sendVotingFinishedMessage"
          />
        </b-col>
      </b-row>

      <h4 v-if="membersPending.length > 0 && !estimateFinished" class="d-inline">
        {{ t("page.session.during.estimation.message.waitingFor") }}
        {{ membersPending.length }} /
        {{ membersPending.length + membersEstimated.length }}
      </h4>

      <b-row
        v-if="!estimateFinished"
        class="d-flex justify-content-center overflow-auto"
        :class="isMobile ? 'avatar-maxHeight' : ''"
      >
        <kick-user-wrapper
          v-for="member of membersPending"
          :key="member.memberID"
          :class="isMobile ? 'm-4' : 'spaceBetweenAvatar'"
          child="RoundedAvatar"
          :member="member"
        />
      </b-row>
      <hr class="my-5 breakingLine" />
      <h4 v-if="!hostVoting">
        {{ t("page.session.during.estimation.message.finished") }}
        {{ membersEstimated.length }} /
        {{ members.length }}
      </h4>
      <h4 v-else>
        <div v-if="hostEstimation == ''">
          {{ t("page.session.during.estimation.message.finished") }}
          {{ membersEstimated.length }} /
          {{ members.length + 1 }}
        </div>
        <div v-else>
          {{ t("page.session.during.estimation.message.finished") }}
          {{ membersEstimated.length + 1 }} /
          {{ members.length + 1 }}
        </div>
      </h4>
      <b-row
        v-if="highlightedMembers.includes(adminID)"
        class="my-1 d-flex justify-content-center flex-wrap overflow-auto kick-user"
        style="max-height: 500px"
      >
        <session-admin-card
          v-if="(estimateFinished && hostVoting) || hostEstimation !== ''"
          :current-estimation="hostEstimation"
          :estimate-finished="estimateFinished"
          :highlight="highlightedMembers.includes(adminID) || highlightedMembers.length === 0"
        />
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
      <b-row
        v-else
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
        <session-admin-card
          v-if="(estimateFinished && hostVoting) || hostEstimation !== ''"
          :current-estimation="hostEstimation"
          :estimate-finished="estimateFinished"
          :highlight="highlightedMembers.includes(adminID) || highlightedMembers.length === 0"
        />
      </b-row>
      <div v-if="hostVoting && !estimateFinished">
        <div v-if="!estimateFinished">
          <hr class="breakingLine" />
          <h4 class="d-inline">Your Estimation</h4>
        </div>
        <div v-if="!estimateFinished" class="newVotes m-1">
          <b-button
            v-for="item in voteSet"
            :key="item"
            variant="primary"
            class="activePills m-1"
            pill
            style="width: 60px"
            @click="vote(item)"
          >
            {{ item }}
          </b-button>
        </div>
      </div>
    </div>
    <b-row v-if="userStoryMode !== 'NO_US'" class="mt-4">
      <b-col>
        <user-story-sum-component />
      </b-col>
    </b-row>
    <b-row v-if="userStoryMode !== 'NO_US'">
      <b-col v-if="!isMobile" cols="7">
        <user-stories
          :card-set="voteSet"
          :show-estimations="planningStart"
          :initial-stories="userStories"
          :show-edit-buttons="true"
          :select-story="true"
          :story-mode="userStoryMode"
          @userStoriesChanged="onUserStoriesChanged"
          @selectedStory="onSelectedStory($event)"
        />
        <div v-if="userStoryMode === 'US_JIRA'" class="refreshUserstories">
          <b-button
            variant="primary"
            class="w-100 mb-3"
            @click="
              refreshUserStories();
              $event.target.blur();
            "
          >
            {{ t("page.session.before.refreshStories") }}
          </b-button>
        </div>
      </b-col>
      <b-col v-else cols="12">
        <user-stories
          :card-set="voteSet"
          :show-estimations="planningStart"
          :initial-stories="userStories"
          :show-edit-buttons="true"
          :select-story="true"
          :story-mode="userStoryMode"
          @userStoriesChanged="onUserStoriesChanged"
          @selectedStory="onSelectedStory($event)"
        />
        <div v-if="userStoryMode === 'US_JIRA'" class="refreshUserstories">
          <b-button
            variant="primary"
            class="w-100 mb-3"
            @click="
              refreshUserStories();
              $event.target.blur();
            "
          >
            {{ t("page.session.before.refreshStories") }}
          </b-button>
        </div>
      </b-col>
    </b-row>
    <b-spinner
      v-if="showSpinner"
      variant="primary"
      class="position-absolute centerSpinner"
      />
    <GptModal
      v-if="showGPTModal"
      :suggestion-description="alternateDescription"
      :gpt-mode="descriptionMode"
      :retry-repaint="updateComponent"
      @acceptSuggestionDescription="acceptSuggestionDescription"
      @retry="retrySuggestionDescription"
      @hideModal="closeModal"
    />
    <b-row>
      <b-col v-if="!isMobile" class="my-5" cols="10">
        <user-story-title
          :alternate-title="alternateTitle"
          :display-ai-option="gptTitleResponse"
          :host="true"
          :initial-stories="userStories"
          :card-set="voteSet"
          :index="index"
          @userStoriesChanged="onUserStoriesChanged"
          @improveTitle="improveTitle"
          @acceptTitle="acceptSuggestionTitle"
          @adjustTitle="adjustOriginalTitle"
          @retryTitle="retryImproveTitle"
          @deleteTitle="deleteTitle"
        />
        <user-story-descriptions
          :initial-stories="userStories"
          :edit-description="true"
          :index="index"
          :gpt-description-response="gptDescriptionResponse"
          :update-component="updateComponent"
          :accepted-stories="acceptedStoriesDescription"
          :isJiraSelected="isJiraSelected"
          @userStoriesChanged="onUserStoriesChanged"
          @sendGPTDescriptionRequest="improveDescription"
        />
      </b-col>
      <b-col v-else class="my-5" cols="12">
        <user-story-title
          :alternate-title="alternateTitle"
          :display-ai-option="gptTitleResponse"
          :host="true"
          :initial-stories="userStories"
          :card-set="voteSet"
          :index="index"
          @userStoriesChanged="onUserStoriesChanged"
          @improveTitle="improveTitle"
          @acceptTitle="acceptSuggestionTitle"
          @adjustTitle="adjustOriginalTitle"
          @retryTitle="retryImproveTitle"
          @deleteTitle="deleteTitle"
        />
        <user-story-descriptions
          :initial-stories="userStories"
          :edit-description="true"
          :index="index"
          :gpt-description-response="gptDescriptionResponse"
          :update-component="updateComponent"
          :isJiraSelected="isJiraSelected"
          @userStoriesChanged="onUserStoriesChanged"
          @sendGPTDescriptionRequest="improveDescription"
        />
      </b-col>
    </b-row>
    <notify-host-component />
  </b-container>
</template>

<script lang="ts">
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
import KickUserWrapper from "@/components/KickUserWrapper.vue";
import SessionCloseButton from "@/components/actions/SessionCloseButton.vue";
import SessionStartButton from "@/components/actions/SessionStartButton.vue";
import { BIconArrowClockwise, BIconBarChartFill } from "bootstrap-vue";
import { defineComponent } from "vue";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";
import SessionAdminCard from "@/components/SessionAdminCard.vue";
import GptModal from "@/components/GptModal.vue";
import UserStoryTitle from "@/components/UserStoryTitle.vue";
import j2m from "jira2md";

export default defineComponent({
  name: "SessionPage",
  components: {
    UserStoryTitle,
    GptModal,
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
    SessionAdminCard,
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    return { store, toast, t };
  },
  data() {
    return {
      sessionID: history.state.sessionID,
      adminID: history.state.adminID,
      voteSetJson: history.state.voteSetJson,
      sessionState: history.state.sessionState,
      timerSecondsString: history.state.timerSecondsString,
      startNewSessionOnMountedString: history.state.startNewSessionOnMountedString,
      userStoryMode: history.state.userStoryMode,
      hostVoting: history.state.hostVoting as boolean,
      rejoined: history.state.rejoined,
      index: 0,
      stageLabelReady: "Ready",
      stageLabelWaiting: "Waiting room",
      planningStart: false,
      voteSet: [] as string[],
      timerCountdownNumber: 0,
      startTimerOnComponentCreation: true,
      estimateFinished: false,
      session: {},
      hostEstimation: "",
      autoReveal: false,
      //needed for jira converter
      isJiraSelected: history.state.isJiraSelected as boolean,
      //generell needed for GPT usage
      showGPTModal: false,
      gptMode: "",
      // needed for title + anti spam
      gptTitleResponse: false,
      alternateTitle: "",
      //needed for description + anti spam
      gptDescriptionResponse: false,
      alternateDescription: "",
      descriptionMode: "",
      updateComponent: false,
      acceptedStoriesDescription: [] as Array<{
        storyID: string | null,
        issueType: string,
      }>,
      showSpinner: false,
      // needed for privacy feature
      confidentialData: {} as Map<string, string>,
      // needed for multi-language GPT
      userStoryLanguage: "",
    };
  },
  computed: {
    selectedProject() {
      return this.store.selectedProject;
    },
    userStories() {
      return this.store.userStories;
    },
    members() {
      return this.store.members;
    },
    webSocketIsConnected() {
      return this.store.webSocketConnected;
    },
    highlightedMembers() {
      return this.store.highlightedMembers;
    },
    membersPending(): Member[] {
      return this.members.filter((member: Member) => member.currentEstimation === null);
    },
    membersEstimated(): Member[] {
      return this.members.filter((member: Member) => member.currentEstimation !== null);
    },
    timerTimestamp() {
      return this.store.timerTimestamp ? this.store.timerTimestamp : "";
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
          this.subscribeOnTimerStart();
          this.subscribeWSNotification();
          if (this.startNewSessionOnMountedString === "true") {
            this.sendRestartMessage();
          }
          setTimeout(() => {
            this.requestMemberUpdate();
          }, 400);
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
      if (this.membersPending.length === 0 && this.membersEstimated.length > 0 && this.autoReveal) {
        if (this.hostVoting && this.hostEstimation !== "") {
          this.estimateFinished = true;
        } else if (!this.hostVoting) {
          this.estimateFinished = true;
        }
      }
    },
  },
  async created() {
    this.store.clearStoreWithoutUserStories();
    if (!this.sessionID || !this.adminID) {
      //check for cookie
      await this.checkAdminCookie();
      this.assignSessionToData(this.session);
      if (this.sessionID?.length === 0) {
        this.goToLandingPage();
      } else {
        this.handleReload();
      }
    }
    this.timerCountdownNumber = parseInt(this.timerSecondsString ?? "0", 10);
    window.addEventListener("beforeunload", this.sendUnregisterCommand);
  },
  mounted() {
    if (this.rejoined === "false") {
      this.connectToWebSocket();
    }
    if (this.voteSetJson) {
      this.voteSet = JSON.parse(this.voteSetJson);
    }
    if (this.sessionState === Constants.memberUpdateCommandStartVoting) {
      this.planningStart = true;
    } else if (this.sessionState === Constants.memberUpdateCommandVotingFinished) {
      this.planningStart = true;
      console.log("ON MOUNTED");
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
    assignSessionToData(session) {
      if (Object.keys(session).length !== 0) {
        this.adminID = session.adminID;
        this.sessionID = session.sessionID;
        this.sessionState = session.sessionState;
        this.timerSecondsString = session.sessionConfig.timerSeconds.toString();
        this.voteSetJson = JSON.stringify(session.sessionConfig.set);
        this.userStoryMode = session.sessionConfig.userStoryMode;
        this.hostVoting = String(session.hostVoting).toLowerCase() === "true";

        this.store.setUserStories({ stories: session.sessionConfig.userStories });
        this.voteSet = JSON.parse(this.voteSetJson);
      }
    },
    handleReload() {
      if (
        this.sessionState === Constants.memberUpdateCommandStartVoting ||
        this.sessionState === Constants.memberUpdateCommandVotingFinished
      ) {
        this.planningStart = true;
      }
      if (this.sessionState === Constants.memberUpdateCommandVotingFinished) {
        this.estimateFinished = true;
      }
      this.timerCountdownNumber = parseInt(this.timerSecondsString ?? "0", 10);
      //reconnect and reload member
      this.connectToWebSocket();
    },
    async onUserStoriesChanged({ us, idx, doRemove }) {
      console.log(`stories: ${us}`);
      console.log(`idx: ${idx}`);
      console.log(`doRemove: ${doRemove}`);
      console.log(`Syncing ${us[idx]}`);
      //Jira sync
      if (this.userStoryMode === "US_JIRA") {
        let response;
        if (doRemove) {
          if (us[idx].id === null) {
            response = 204;
          } else {
            response = await apiService.deleteUserStory(us[idx].id);
          }
          us.splice(idx, 1);
          doRemove = false;
        } else {
          console.log(`ID: ${us[idx].id}`);
          if (us[idx].id === null && this.selectedProject?.id) {
            response = await apiService.createUserStory(
              JSON.stringify(us[idx]),
              this.selectedProject.id
            );
            if (response.status === 200) {
              us = this.userStories.map((s) =>
                s.title === us[idx].title && s.description === us[idx].description
                  ? { ...s, id: response.data }
                  : s
              );
              console.log(`assigned id: ${us[idx].id}`);
            }
          } else {
            if (this.isJiraSelected && us[idx].description) {
              us[idx].description = j2m.to_jira(us[idx].description);
            }
            response = await apiService.updateUserStory(JSON.stringify(us[idx]));
          }
        }
        if (response.status === 200) {
          this.toast.success(
            this.t("session.notification.messages.issueTrackerSynchronizeSuccess")
          );
        } else if (response === 204) {
          this.toast.info(this.t("session.notification.messages.issueTrackerNothingChanged"));
        } else {
          this.toast.error(this.t("session.notification.messages.issueTrackerSynchronizeFailed"));
        }
      }
      // WS send
      if (doRemove) {
        us.splice(idx, 1);
      }
      this.store.setUserStories({ stories: us });
      if (this.webSocketIsConnected) {
        if (this.isJiraSelected && us[idx].description) {
          us[idx].description = j2m.to_jira(us[idx].description);
        }
        const endPoint = `${Constants.webSocketAdminUpdatedUserStoriesRoute}`;
        this.store.sendViaBackendWS(endPoint, JSON.stringify(us));
      }
    },
    async refreshUserStories() {
      const response = await apiService.getUserStoriesFromProject(this.selectedProject?.name);
      this.store.setUserStories({ stories: response });
      if (this.webSocketIsConnected) {
        const endPoint = `${Constants.webSocketAdminUpdatedUserStoriesRoute}`;
        this.store.sendViaBackendWS(endPoint, JSON.stringify(response));
      }
    },
    onSelectedStory($event) {
      if (this.planningStart) {
        const endPoint = Constants.webSocketAdminSelectedUserStoryRoute;
        this.store.sendViaBackendWS(endPoint, $event);
      }
      this.index = $event;
    },
    connectToWebSocket() {
      const url = `${Constants.backendURL}/connect?sessionID=${this.sessionID}&adminID=${this.adminID}`;
      this.store.connectToBackendWS(url);
    },
    registerAdminPrincipalOnBackend() {
      const endPoint = Constants.webSocketRegisterAdminUserRoute;
      this.store.sendViaBackendWS(endPoint);
    },
    subscribeWSMemberUpdated() {
      this.store.subscribeOnBackendWSAdminUpdate();
    },
    subscribeOnTimerStart() {
      this.store.subscribeOnBackendWSTimerStart();
    },
    requestMemberUpdate() {
      const endPoint = Constants.webSocketGetMemberUpdateRoute;
      this.store.sendViaBackendWS(endPoint);
    },
    subscribeWSNotification() {
      this.store.subscribeOnBackendWSNotify();
    },
    sendUnregisterCommand() {
      const endPoint = `${Constants.webSocketUnregisterRoute}`;
      this.store.sendViaBackendWS(endPoint);
      this.store.clearStore();
    },
    sendVotingFinishedMessage() {
      if (!this.estimateFinished) {
        this.estimateFinished = true;
        const endPoint = Constants.webSocketVotingFinishedRoute;
        this.store.sendViaBackendWS(endPoint);
      }
    },
    sendRestartMessage() {
      this.estimateFinished = false;
      this.hostEstimation = "";
      const endPoint = Constants.webSocketRestartPlanningRoute;
      this.store.sendViaBackendWS(
        endPoint,
        JSON.stringify({
          hostVoting: this.hostVoting,
          autoReveal: this.autoReveal,
        })
      );
    },
    goToLandingPage() {
      this.$router.push({ name: "LandingPage" });
    },
    onPlanningStarted() {
      this.planningStart = true;
    },
    vote(vote: string) {
      this.hostEstimation = vote;
      const endPoint = `${Constants.webSocketVoteRouteAdmin}`;
      this.store.sendViaBackendWS(
        endPoint,
        JSON.stringify({
          vote: this.hostEstimation,
          autoReveal: this.autoReveal,
        })
      );
    },
    async improveTitle({ userStory, confidentialInformation }) {
      const trimmedStoryTitle = userStory.title.trim();
      if (trimmedStoryTitle.length > 0) {
        const response = await apiService.improveTitle(userStory, confidentialInformation);
        this.alternateTitle = response.data.improvedTitle;
        this.gptTitleResponse = true;
      }
    },
    acceptSuggestionTitle({ id }) {
      this.userStories
        .filter((us) => {
          us.id !== id;
        })
        .map((us) => {
          us.title = this.alternateTitle;
        });
      this.gptTitleResponse = false;
      this.alternateTitle = "";
      this.onUserStoriesChanged({ us: this.userStories, idx: this.index, doRemove: false });
    },
    adjustOriginalTitle() {
      this.alternateTitle = "";
      this.gptTitleResponse = false;
    },
    async retryImproveTitle({ id, originalTitle, confidentialData }) {
      this.gptTitleResponse = false;
      const userstory = this.userStories.find((us) => us.id === id);
      if (userstory) {
        const response = await apiService.retryImproveTitle(userstory, originalTitle, confidentialData);
        this.alternateTitle = response.data.improvedTitle;
        this.gptTitleResponse = true;
      }
    },
    deleteTitle() {
      this.alternateTitle = "";
      this.gptTitleResponse = false;
    },
    async improveDescription({ userStory, description, issue, confidentialData, language }) {
      this.userStoryLanguage = language;
      this.confidentialData = confidentialData;
      this.showSpinner = true;
      if (issue === 'improveDescription') {
        const response = await apiService.improveDescription(userStory, description, this.confidentialData, language);
        this.alternateDescription =
          response.description + response.acceptance_criteria.toString().replaceAll(",", "");
      } else {
        const response = await apiService.grammarCheck(userStory, description, this.confidentialData, language);
        this.alternateDescription = response.description;
      }
      this.descriptionMode = issue;
      this.gptDescriptionResponse = true;
      this.showSpinner = false;
      this.showGPTModal = true;
    },
    acceptSuggestionDescription({ description, originalText }) {
      if (originalText) {
        this.userStories[this.index].description = this.alternateDescription;
      } else {
        this.userStories[this.index].description = description;
      }
      this.onUserStoriesChanged({ us: this.userStories, idx: this.index, doRemove: false });
      this.updateComponent = !this.updateComponent;
      this.acceptedStoriesDescription.push({ storyID: this.userStories[this.index].id, issueType: this.descriptionMode})
    },
    async retrySuggestionDescription() {
      await this.improveDescription({userStory: this.userStories[this.index],description: this.userStories[this.index].description, issue: this. descriptionMode, confidentialData: this.confidentialData, language: this.userStoryLanguage});
      this.updateComponent = !this.updateComponent;
    },
    closeModal() {
      this.showGPTModal = false;
      this.gptDescriptionResponse = false;
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>

.centerSpinner {
  left: 0;
  right: 10%;
  top: 57%;
  bottom: 0;
  margin: auto;
  z-index: 3;
}

.spaceBetweenAvatar {
  margin-right: 2em;
  margin-left: 2em;
}

.avatar-maxHeight {
  max-height: 500px;
}

.newVotes {
  text-align: center;
  margin-left: auto;
  margin-right: auto;
}

.activePills {
  &:not(.active) {
    background-color: var(--preparePageNotSelectedTabBackground) !important;
    color: var(--text-primary-color) !important;

    &:hover {
      color: var(--text-color-hover) !important;
    }

    &:focus {
      background-color: var(--primary-button) !important;
    }
  }
}

.optionButtonCol {
  margin-top: auto;
  margin-bottom: auto;
}

.breakingLine {
  border-color: var(--text-primary-color);
}

.copy-popup {
  text-align: center;
}

.kick-user {
  max-height: 500px;
}

.headers {
  display: flex;
  align-items: center;
  min-height: 20vh;
  margin-right: 130px;
}

.bIcons {
  height: 40px;
  width: 40px;
}

.optionButton {
  background-color: var(--textAreaColour);
  display: inline-flex;
  align-items: center;
}

.optionButton:hover {
  background-color: var(--textAreaColourHovered);
  color: var(--text-primary-color);
}

.optionButton:focus {
  background-color: var(--textAreaColourHovered) !important;
  color: var(--text-primary-color) !important;
}

.optionButton:disabled {
  background-color: var(--textAreaColourHovered) !important;
  color: var(--text-primary-color) !important;
}

#catGifDiv {
  text-align: center;
}

.catGif {
  width: 240px;
  height: 180px;
}
</style>
