<template class="main">
  <b-container>
    <b-overlay :show="isJoining">
      <template #overlay>
        <b-spinner class="me-2" />
        <span class="overlayText">
          {{ t("session.connection.connecting") }}
        </span>
      </template>
      <h1 id="heading">
        {{ t("page.join.title") }}
        <i id="controller" class="bi bi-controller"></i>
      </h1>
      <join-page-card
        :color="hexColor"
        :animal-asset-name="avatarAnimalAssetName"
        :button-text="t('page.join.submit')"
        :session-id-from-url="sessionID"
        @clicked="sendJoinSessionRequest"
      />
    </b-overlay>
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { v4 as uuidv4 } from "uuid";
import JoinPageCard from "../components/JoinPageCard.vue";
import JoinCommand from "../model/JoinCommand";
import Constants from "../constants";
import axios from "axios";
import { useDiveniStore } from "@/store";
import { webSocketService, ConnectionState } from "@/services/WebSocketService";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";
import { useRouter, useRoute } from "vue-router";

export default defineComponent({
  name: "JoinPage",
  components: {
    JoinPageCard,
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    const route = useRoute();
    const router = useRouter();
    return { store, toast, t, route, router };
  },
  data() {
    return {
      isJoining: false,
      hexColor: Constants.getRandomPastelColor(),
      avatarAnimalAssetName: Constants.getRandomAvatarAnimalAssetName(),
      memberID: uuidv4(),
      name: "",
      sessionID: "",
      voteSet: "",
      timerSeconds: 0,
      userStoryMode: "",
    };
  },
  computed: {
    webSocketIsConnected() {
      return webSocketService.connectionState.value === ConnectionState.CONNECTED;
    },
  },
  watch: {
    webSocketIsConnected(isConnected) {
      if (isConnected) {
        this.registerMemberPrincipalOnBackend();
        this.goToEstimationPage();
      }
    },
  },
  created() {
    const id = this.route.query as unknown as { sessionID: string };
    if (id.sessionID) {
      this.sessionID = id.sessionID;
    }
  },
  methods: {
    async sendJoinSessionRequest(data: JoinCommand) {
      this.isJoining = true;
      await this.store.clearStore();
      this.name = data.name;
      const url = `${Constants.backendURL}${Constants.joinSessionRoute(data.sessionID)}`;
      const joinInfo = {
        password: data.password,
        member: {
          memberID: this.memberID,
          name: this.name,
          hexColor: this.hexColor,
          avatarAnimal: this.convertAvatarAssetNameToBackendAnimal(),
          currentEstimation: null,
        },
      };
      try {
        const result = await axios.post(url, joinInfo);

        const sessionConfig = result.data as {
          set: Array<string>;
          timerSeconds: number;
          userStories: Array<{ title: string; description: string; estimation: string | null }>;
          userStoryMode: string;
        };
        this.voteSet = JSON.stringify(sessionConfig.set);
        this.timerSeconds = parseInt(JSON.stringify(sessionConfig.timerSeconds), 10);
        this.userStoryMode = sessionConfig.userStoryMode;
        this.store.setUserStories({
          stories: sessionConfig.userStories,
        });
        localStorage.setItem(
          "diveni_member_session",
          JSON.stringify({
            sessionID: data.sessionID,
            memberID: this.memberID,
            name: this.name,
            hexColor: this.hexColor,
            avatarAnimalAssetName: this.avatarAnimalAssetName,
            voteSetJson: this.voteSet,
            timerSecondsString: this.timerSeconds.toString(),
            userStoryMode: this.userStoryMode,
          })
        );
        this.connectToWebSocket(data.sessionID, joinInfo.member.memberID);
      } catch (e) {
        this.isJoining = false;
        console.error(`Response of ${url} is invalid: ${e}`);
        this.showToast(e);
      }
    },
    convertAvatarAssetNameToBackendAnimal() {
      return Constants.avatarAnimalAssetNameToBackendEnum(this.avatarAnimalAssetName);
    },
    connectToWebSocket(sessionID: string, memberID: string) {
      this.store.subscribeOnBackendWSMemberUpdates();
      this.store.subscribeOnBackendWSMemberUpdatesWithAutoReveal();
      this.store.subscribeOnBackendWSStoriesUpdated();
      this.store.subscribeOnBackendWSStorySelected();
      this.store.subscribeOnBackendWSAdminUpdate();
      this.store.subscribeOnBackendWSTimerStart();
      this.store.subscribeOnBackendWSNotify();
      this.store.subscribeOnBackendWSHostVoting();
      this.store.subscribeOnBackendWSHostEstimation();
      const url = `${Constants.backendURL}/connect?sessionID=${sessionID}&memberID=${memberID}`;
      this.store.connectToBackendWS(url);
    },
    registerMemberPrincipalOnBackend() {
      const endPoint = Constants.webSocketRegisterMemberRoute;
      this.store.sendViaBackendWS(endPoint);
    },
    goToEstimationPage() {
      this.router.push({
        name: "MemberVotePage",
        state: {
          memberID: this.memberID,
          name: this.name,
          hexColor: this.hexColor,
          avatarAnimalAssetName: this.avatarAnimalAssetName,
          voteSetJson: this.voteSet,
          timerSecondsString: this.timerSeconds.toString(),
          userStoryMode: this.userStoryMode,
        },
      });
    },
    showToast(e) {
      if (e.message == "Request failed with status code 404") {
        this.toast.error(this.t("session.notification.messages.wrongID"));
      }
      if (e.message == "Request failed with status code 401") {
        this.toast.error(this.t("session.notification.messages.password"));
      }
      console.error(e);
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.overlayText {
  font-size: 2em;
  margin: 0.67em 0;
  font-weight: bold;
}

#heading {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  min-height: 10vh;
}

#controller {
  height: 49px;
  width: 78px;
  transform: rotate(315deg);
  margin-left: 1%;
}
</style>
