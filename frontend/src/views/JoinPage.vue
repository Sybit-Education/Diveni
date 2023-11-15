<template class="main">
  <b-container>
    <h1 id="heading">
      {{ $t("page.join.title") }}
      <!-- <b-img :src="require('@/assets/ControllerJoinPage.png')" id="controller"/> -->
      <BIconController id="controller" />
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
import Vue from "vue";
import { v4 as uuidv4 } from "uuid";
import JoinPageCard from "../components/JoinPageCard.vue";
import JoinCommand from "../model/JoinCommand";
import Constants from "../constants";
import { BIconController } from "bootstrap-vue";

export default Vue.extend({
  name: "JoinPage",
  components: {
    JoinPageCard,
    BIconController,
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
        this.subscribeWSMemberUpdatesWithAutoReveal();
        this.subscribeWSadminUpdatedUserStories();
        this.subscribeWSStorySelected();
        this.subscribeWSMemberUpdated();
        this.subscribeOnTimerStart();
        this.subscribeWSNotification();
        this.subscribeWSMemberHostVotingUpdate();
        this.subscribeWSMemberHostEstimation();
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
        const result = await this.axios.post(url, joinInfo);

        const sessionConfig = result.data as {
          set: Array<string>;
          timerSeconds: number;
          userStories: Array<{ title: string; description: string; estimation: string | null }>;
          userStoryMode: string;
        };
        this.voteSet = JSON.stringify(sessionConfig.set);
        this.timerSeconds = parseInt(JSON.stringify(sessionConfig.timerSeconds), 10);
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
      return Constants.avatarAnimalAssetNameToBackendEnum(this.avatarAnimalAssetName);
    },
    connectToWebSocket(sessionID: string, memberID: string) {
      const url = `${Constants.backendURL}/connect?sessionID=${sessionID}&memberID=${memberID}`;
      this.$store.commit("connectToBackendWS", url);
    },
    registerMemberPrincipalOnBackend() {
      const endPoint = Constants.webSocketRegisterMemberRoute;
      this.$store.commit("sendViaBackendWS", { endPoint });
    },
    subscribeWSMemberHostVotingUpdate() {
      this.$store.commit("subscribeOnBackendWSHostVoting");
    },
    subscribeWSMemberHostEstimation() {
      this.$store.commit("subscribeOnBackendWSHostEstimation");
    },
    subscribeWSMemberUpdates() {
      this.$store.commit("subscribeOnBackendWSMemberUpdates");
    },
    subscribeWSMemberUpdatesWithAutoReveal() {
      this.$store.commit("subscribeOnBackendWSMemberUpdatesWithAutoReveal");
    },
    subscribeWSNotification() {
      this.$store.commit("subscribeOnBackendWSNotify");
    },
    subscribeWSadminUpdatedUserStories() {
      this.$store.commit("subscribeOnBackendWSStoriesUpdated");
    },
    subscribeWSStorySelected() {
      this.$store.commit("subscribeOnBackendWSStorySelected");
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
        this.$toast.error(this.$t("session.notification.messages.wrongID"));
      }
      if (e.message == "Request failed with status code 401") {
        this.$toast.error(this.$t("session.notification.messages.password"));
      }
      console.error(e);
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
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
