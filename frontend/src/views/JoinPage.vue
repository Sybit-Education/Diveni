<template>
  <b-container>
    <h1 class="my-5 mx-2">
      {{ title }}
    </h1>
    <join-page-card
      :color="hexColor"
      :animal-asset-name="avatarAnimalAssetName"
      :button-text="'GO'"
      :session-id-from-url="sessionID"
      @clicked="sendJoinSessionRequest"
    />
  </b-container>
</template>

<script lang="ts">
import Vue from 'vue';
import { v4 as uuidv4 } from 'uuid';
import JoinPageCard from '../components/JoinPageCard.vue';
import JoinCommand from '../model/JoinCommand';
import Constants from '../constants';

export default Vue.extend({
  name: 'JoinPage',
  components: {
    JoinPageCard,
  },
  data() {
    return {
      title: 'Join a meeting ...',
      hexColor: Constants.getRandomPastelColor(),
      avatarAnimalAssetName: Constants.getRandomAvatarAnimalAssetName(),
      memberID: uuidv4(),
      name: '',
      sessionID: '',
      voteSet: '',
      userStories: '',
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
        console.debug('JoinPage: member connected to websocket');
        this.registerMemberPrincipalOnBackend();
        this.subscribeWSStartEstimating();
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
        const sessionConfig = (await this.axios.post(url, joinInfo)).data as {
          set: Array<string>,
          userStories: Array<{ title: string, description: string, estimation: string | null }>,
        };
        this.voteSet = JSON.stringify(sessionConfig.set);
        this.userStories = JSON.stringify(sessionConfig.userStories);
        this.connectToWebSocket(data.sessionID, joinInfo.member.memberID);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    convertAvatarAssetNameToBackendAnimal() {
      return Constants.avatarAnimalAssetNameToBackendEnum(
        this.avatarAnimalAssetName,
      );
    },
    connectToWebSocket(sessionID: string, memberID: string) {
      const url = `${Constants.backendURL}/connect?sessionID=${sessionID}&memberID=${memberID}`;
      this.$store.commit('connectToBackendWS', url);
    },
    registerMemberPrincipalOnBackend() {
      const endPoint = Constants.webSocketRegisterMemberRoute;
      this.$store.commit('sendViaBackendWS', { endPoint });
    },
    subscribeWSStartEstimating() {
      this.$store.commit('subscribeOnBackendWSStartPlanningListenRoute');
    },
    goToEstimationPage() {
      this.$router.push({
        name: 'MemberVotePage',
        params: {
          memberID: this.memberID,
          name: this.name,
          hexColor: this.hexColor,
          avatarAnimalAssetName: this.avatarAnimalAssetName,
          voteSetJson: this.voteSet,
        },
      });
    },
  },
});
</script>
