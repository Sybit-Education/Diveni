<template>
  <b-container id="join-page">
    <h1 class="my-5 mt-2">
      {{ $t("page.join.title") }}
    </h1>
    <join-page-card
      :color="hexColor"
      :animal-asset-name="avatarAnimalAssetName"
      :button-text="$t('page.join.submit')"
      :session-id-from-url="sessionID"
      @clicked="sendJoinSessionRequest"
    />
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { v4 as uuidv4 } from "uuid";
import JoinPageCard from "../components/JoinPageCard.vue";
 import { useToast } from "vue-toastification";
import JoinCommand from "../model/JoinCommand";
import Constants from "../constants";

export default defineComponent({
  name: "JoinPage",
  components: {
    JoinPageCard,
  },
  data() {
    return {
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
      return this.$store.state.webSocketConnected;
    },
  },
  watch: {
    webSocketIsConnected(isConnected) {
      if (isConnected) {
        console.debug("JoinPage: member connected to websocket");
        this.registerMemberPrincipalOnBackend();
        this.subscribeWSMemberUpdates();
        this.subscribeWSadminUpdatedUserStories();
        this.subscribeWSMemberUpdated();
        this.subscribeOnTimerStart();
        this.subscribeWSNotification();
        this.goToEstimationPage();
      }
    },
  },
  created() {
    const id = this.$route.query as unknown as { sessionID: string };
    if (id.sessionID) {
      this.sessionID = id.sessionID;
    }
  },
  methods: {
    async sendJoinSessionRequest(data: JoinCommand) {
      this.$store.commit("clearStore");
      this.name = data.name;
      const url = `${Constants.backendURL}${Constants.joinSessionRoute(
        data.sessionID
      )}`;
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
        const result = await this.axios.post(url, joinInfo);

        const sessionConfig = result.data as {
          set: Array<string>;
          timerSeconds: number;
          userStories: Array<{
            title: string;
            description: string;
            estimation: string | null;
          }>;
          userStoryMode: string;
        };
        this.voteSet = JSON.stringify(sessionConfig.set);
        this.timerSeconds = parseInt(
          JSON.stringify(sessionConfig.timerSeconds),
          10
        );
        this.userStoryMode = sessionConfig.userStoryMode;
        this.$store.commit("setUserStories", {
          stories: sessionConfig.userStories,
        });
        this.connectToWebSocket(data.sessionID, joinInfo.member.memberID);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
        this.showToast(e);
      }
    },
    convertAvatarAssetNameToBackendAnimal() {
      return Constants.avatarAnimalAssetNameToBackendEnum(
        this.avatarAnimalAssetName
      );
    },
    connectToWebSocket(sessionID: string, memberID: string) {
      const url = `${Constants.backendURL}/connect?sessionID=${sessionID}&memberID=${memberID}`;
      this.$store.commit("connectToBackendWS", url);
    },
    registerMemberPrincipalOnBackend() {
      const endPoint = Constants.webSocketRegisterMemberRoute;
      this.$store.commit("sendViaBackendWS", { endPoint });
    },
    subscribeWSMemberUpdates() {
      this.$store.commit("subscribeOnBackendWSMemberUpdates");
    },
    subscribeWSNotification() {
      this.$store.commit("subscribeOnBackendWSNotify");
    },
    subscribeWSadminUpdatedUserStories() {
      this.$store.commit("subscribeOnBackendWSStoriesUpdated");
    },
    subscribeWSMemberUpdated() {
      this.$store.commit("subscribeOnBackendWSAdminUpdate");
    },
    subscribeOnTimerStart() {
      this.$store.commit("subscribeOnBackendWSTimerStart");
    },
    goToEstimationPage() {
      this.$router.push({
        name: "MemberVotePage",
        params: {
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
        useToast().error(this.$t("session.notification.messages.wrongID"));
      }
      if (e.message == "Request failed with status code 401") {
        useToast().error(this.$t("session.notification.messages.password"));
      }
      console.error(e);
    },
  },
});
</script>
