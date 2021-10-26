<template>
  <div>
    <h1 class="my-5 mx-2">
      {{ title }}
    </h1>
    <h4 class="mt-4 mx-2">
      Share the code
      <b-link
        href=""
        @click="copyCodeToClipboard"
      >
        {{ sessionID }}
      </b-link>
      with your team mates.
      <b-icon-unlock />
    </h4>
    <b-container class="my-5">
      <b-row
        class="d-flex justify-content-center border rounded"
        style="min-height: 200px;"
      >
        <SessionMemberCard
          v-for="member of members"
          :key="member.memberID"
          class="m-4"
          :color="member.hexColor"
          :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
          :name="member.name"
        />
      </b-row>
      <b-row>
        <b-col class="text-center mt-5 mb-5">
          <b-button
            variant="success"
            :disabled="false"
            @click="sendStartPlanningMessage"
          >
            Start Planning
          </b-button>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import * as Constants from '../constants';
import SessionMemberCard from '../components/SessionMemberCard.vue';

export default Vue.extend({
  name: 'LandingPage',
  components: {
    SessionMemberCard,
  },
  props: {
    adminID: {
      type: String,
      default: undefined,
    },
    sessionID: {
      type: String,
      default: undefined,
    },
  },
  data() {
    return {
      title: 'Waiting for members ...',
    };
  },
  computed: {
    members() {
      return this.$store.state.members;
    },
    webSocketIsConnected() {
      return this.$store.state.webSocketConnected;
    },
  },
  watch: {
    webSocketIsConnected(isConnected) {
      if (isConnected) {
        console.debug('JoinPage: member connected to websocket');
        this.registerAdminPrincipalOnBackend();
        this.subscribeWSMemberUpdated();
      }
    },
  },
  mounted() {
    if (this.sessionID === undefined || this.adminID === undefined) {
      // TODO: handle when user goes directly to /session and not via landing Page
      // eslint-disable-next-line no-alert
      alert('ids undefined');
    }
    this.connectToWebSocket();
  },
  methods: {
    connectToWebSocket() {
      const url = `${Constants.default.backendURL}/connect?sessionID=${this.sessionID}&adminID=${this.adminID}`;
      this.$store.commit('connectToBackendWS', url);
    },
    registerAdminPrincipalOnBackend() {
      const endPoint = Constants.default.webSocketRegisterAdminUserRoute;
      this.$store.commit('sendViaBackendWS', endPoint, {});
    },
    subscribeWSMemberUpdated() {
      this.$store.commit('subscribeOnBackendWSAdminUpdate');
    },
    sendStartPlanningMessage() {
      const endPoint = Constants.default.webSocketStartPlanningRoute;
      this.$store.commit('sendViaBackendWS', endPoint, {});
    },
    copyCodeToClipboard() {
      navigator.clipboard.writeText(this.sessionID).then(() => {
        console.log('Async: Copying to clipboard was successful!');
      }, (err) => {
        console.error('Async: Could not copy text: ', err);
      });
    },
    backendAnimalToAssetName(animal:string) {
      return Constants.default.avatarAnimalToAssetName(animal);
    },
  },
});
</script>
