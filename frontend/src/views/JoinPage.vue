<template>
  <b-container>
    <h1 class="my-5 mx-2">
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
import Vue from "vue";
import { v4 as uuidv4 } from "uuid";
import JoinPageCard from "../components/JoinPageCard.vue";
import JoinCommand from "../model/JoinCommand";
import Constants from "../constants";

export default Vue.extend({
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
        const response = result.data as {
          sessionConfig: {
            set: Array<string>;
            timerSeconds: number;
            userStories: Array<{
              title: string;
              description: string;
              estimation: string | null;
            }>;
            userStoryMode: string;
          };
          memberCookie: string;
        };
        window.localStorage.setItem("memberCookie", response.memberCookie);
        this.voteSet = JSON.stringify(response.sessionConfig.set);
        this.timerSeconds = parseInt(JSON.stringify(response.sessionConfig.timerSeconds), 10);
        this.userStoryMode = response.sessionConfig.userStoryMode;
        console.log("session page");
        console.log(response);
        this.$store.commit("setUserStories", {
          stories: response.sessionConfig.userStories,
        });
        this.goToEstimationPage();
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
    goToEstimationPage() {
      this.$router.push({
        name: "MemberVotePage",
        params: {
          sessionID: this.sessionID,
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
      console.log(e);
    },
  },
});
</script>
