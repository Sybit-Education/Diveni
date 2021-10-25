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
import SockJS from 'sockjs-client';
import * as webStomp from 'webstomp-client';
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
      webSocketConnected: false,
      stompClient: webStomp.over(new SockJS(`${Constants.default.backendURL}/connect?sessionID=${this.sessionID}&adminID=${this.adminID}`)),
      members: [],
    };
  },
  mounted() {
    if (this.sessionID === undefined || this.adminID === undefined) {
      // TODO: handle when user goes directly to /session and not via landing Page
      // eslint-disable-next-line no-alert
      alert('ids undefined');
    }
    console.debug({ sessionID: this.sessionID });
    this.connectToWebSocketBackend();
  },
  methods: {
    async sendStartPlanningMessage() {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send(Constants.default.webSocketStartPlanningRoute, '', {});
      }
    },
    copyCodeToClipboard() {
      navigator.clipboard.writeText(this.sessionID).then(() => {
        console.log('Async: Copying to clipboard was successful!');
      }, (err) => {
        console.error('Async: Could not copy text: ', err);
      });
    },
    sendJoinAdminCommand() {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send(Constants.default.webSocketRegisterAdminUserRoute, '', {});
      }
    },
    backendAnimalToAssetName(animal:string) {
      return Constants.default.avatarAnimalToAssetName(animal);
    },
    connectToWebSocketBackend() {
      this.stompClient.connect({},
        () => {
          this.webSocketConnected = true;
          this.subscripeToWebSocketBackend();
          this.sendJoinAdminCommand();
        },
        (error) => {
          console.error(error);
          this.webSocketConnected = false;
        });
    },
    subscripeToWebSocketBackend() {
      this.stompClient.subscribe(
        Constants.default.webSocketMembersUpdatedRoute,
        (message: webStomp.Frame) => {
          this.members = JSON.parse(message.body);
        },
      );
    },
    disconnect() {
      if (this.stompClient) {
        this.stompClient.disconnect();
      }
      this.webSocketConnected = false;
    },
  },
});
</script>
