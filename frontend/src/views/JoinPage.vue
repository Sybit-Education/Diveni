<template>
  <div>
    <h1 class="my-5 mx-2">
      {{ title }}
    </h1>
    <b-container>
      <join-page-card
        :show-password="false"
        :color="hexColor"
        :animal-asset-name="avatarAnimalAssetName"
        :button-text="'GO'"
        @clicked="sendJoinSessionRequest"
      />
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import { v4 as uuidv4 } from 'uuid';
import JoinPageCard from '../components/JoinPageCard.vue';
import JoinCommand from '../model/JoinCommand';
import * as Constants from '../constants';

export default Vue.extend({
  name: 'JoinPage',
  components: {
    JoinPageCard,
  },
  data() {
    return {
      title: 'Join a meeting ...',
      hexColor: Constants.default.getRandomPastelColor(),
      avatarAnimalAssetName: Constants.default.getRandomAvatarAnimalAssetName(),
    };
  },
  computed: {
    webSocketIsConnected() {
      return this.$store.state.webSocketConnected;
    },
    startPlanning() {
      return this.$store.state.startPlanning;
    },
  },
  watch: {
    webSocketIsConnected(isConnected) {
      if (isConnected) {
        console.debug('JoinPage: member connected to websocket');
        this.registerPrincipalOnBackend();
        this.goToEstimationPage();
      }
    },
    startPlanning(message) {
      console.debug(`JoinPage: ${message}`);
    },
  },
  methods: {
    async sendJoinSessionRequest(data: JoinCommand) {
      const url = `${Constants.default.backendURL}${Constants.default.joinSessionRoute(data.sessionID)}`;
      const member = {
        memberID: uuidv4(),
        name: data.name,
        hexColor: this.hexColor,
        avatarAnimal: this.convertAvatarAssetNameToBackendAnimal(),
        currentEstimation: null,
      };
      try {
        await this.axios.post(url, member);
        this.connectToWebSocket(data.sessionID, member.memberID);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    convertAvatarAssetNameToBackendAnimal() {
      return Constants.default.avatarAnimalAssetNameToBackendEnum(
        this.avatarAnimalAssetName,
      );
    },
    connectToWebSocket(sessionID: string, memberID: string) {
      const url = `${Constants.default.backendURL}/connect?sessionID=${sessionID}&memberID=${memberID}`;
      this.$store.commit('connectToBackendWS', url);
    },
    registerPrincipalOnBackend() {
      const endPoint = Constants.default.webSocketRegisterMemberRoute;
      this.$store.commit('sendViaBackendWS', endPoint, {});
    },
    subscribeWSStartPlanning() {
      this.$store.commit('subscribeOnBackendWSStartPlanningListenRoute');
    },
    goToEstimationPage() {
      console.log('Would go to estimation page');
    },
  },
});
</script>
