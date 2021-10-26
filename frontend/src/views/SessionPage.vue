<template>
  <div>
    <span v-if="!planningStart">
      <h1 class="my-5 mx-2">
        {{ titleWaiting }}
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
          <SessionMemberCircle
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
    </span>
    <span
      v-if="planningStart"
    >
      <h1 class="mt-5 mb-3 mx-2">
        {{ titleEstimate }}
      </h1>
      <b-button
        variant="outline-dark"
        class="ml-5 pl-5"
        @click="getNumPerRow"
      >
        <b-icon-arrow-clockwise /> New
      </b-button>
      <b-container class="my-5">
        <b-row
          id="grid"
          class="d-flex justify-content-center border rounded"
          style="min-height: 200px;"
        >
          <SessionMemberCard
            v-for="(member, index) of members"
            :key="member.memberID"
            class="m-4"
            :style="{top: -1 * ((Math.trunc(index/grid))) * 200+'px', zIndex: -1*index}"
            :color="member.hexColor"
            :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
            :alt-attribute="member.avatarAnimal"
            :name="member.name"
            :index="index"
          />
        </b-row>
      </b-container>
    </span>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import SockJS from 'sockjs-client';
import * as webStomp from 'webstomp-client';
import * as Constants from '../constants';
import SessionMemberCircle from '../components/SessionMemberCircle.vue';
import SessionMemberCard from '../components/SessionMemberCard.vue';

export default Vue.extend({
  name: 'LandingPage',
  components: {
    SessionMemberCircle,
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
      titleWaiting: 'Waiting for members ...',
      titleEstimate: 'Estimtate!',
      grid: 5,
      webSocketConnected: false,
      stompClient: webStomp.over(new SockJS(`${Constants.default.backendURL}/connect?sessionID=${this.sessionID}&adminID=${this.adminID}`)),
      members: [
        {
          name: 'Tom',
          avatarAnimal: 'DUCK',
          currentEstimation: null,
          hexColor: '#e7e0d2',
          memberID: 'b11c0c20-df81-4417-9153-7c1hnh10c7c1833',
        },
        {
          name: 'John',
          avatarAnimal: 'turtle',
          currentEstimation: null,
          hexColor: 'rgb(245, 245, 230)',
          memberID: 'b11c0c20-df81-4417-9153-7c11nh0c7c23833',
        },
        {
          name: 'Bud',
          avatarAnimal: 'DUCK',
          currentEstimation: null,
          hexColor: 'rgb(246, 233, 246)',
          memberID: 'b11c0c20-df81-4417-9153-7cgf110c7c1833',
        },
        {
          name: 'Doe',
          avatarAnimal: 'turtle',
          currentEstimation: null,
          hexColor: '#e7e0d2',
          memberID: 'b11c0c20-df81-4417-9bg153-7cdf110c7c23833',
        },
        {
          name: 'Pogo',
          avatarAnimal: 'DUCK',
          currentEstimation: null,
          hexColor: 'rgb(246, 233, 246)',
          memberID: 'b11c0c20-dfdd81-4417-9153-7bgbcgf110c7c1833',
        },
        {
          name: 'Blue',
          avatarAnimal: 'turtle',
          currentEstimation: null,
          hexColor: '#e7e0d2',
          memberID: 'b11c0c20-df81-44vd17-9153-7cdf110c7nhc23833',
        },
        {
          name: 'Blue',
          avatarAnimal: 'turtle',
          currentEstimation: null,
          hexColor: '#e7e0d2',
          memberID: 'b11c0c20-df81-44vd17-9153-7cdf110c7bgbc23833',
        },
        {
          name: 'Blue',
          avatarAnimal: 'turtle',
          currentEstimation: null,
          hexColor: '#e7e0d2',
          memberID: 'b11c0c20-vfvdf81-44vd17-9153-7cdf110c7c23833',
        },
        {
          name: 'Blue',
          avatarAnimal: 'turtle',
          currentEstimation: null,
          hexColor: '#e7e0d2',
          memberID: 'b11c0c20-df81-44vd17-9153-7cdfvfvf110c7c23833',
        },
        {
          name: 'Blue',
          avatarAnimal: 'turtle',
          currentEstimation: null,
          hexColor: '#e7e0d2',
          memberID: 'b11c0c20-df81-44vd17-91vfv53-7cdf110c7c23833',
        },
      ],
      planningStart: false,
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
        this.planningStart = true;
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
    getNumPerRow() {
      const grid = Array.from((document.querySelector('#grid') as HTMLElement).children);
      const baseOffset = (grid[0] as HTMLElement).offsetTop;
      const breakIndex = grid.findIndex((item) => (item as HTMLElement).offsetTop > baseOffset);
      this.grid = (breakIndex === -1 ? grid.length : breakIndex);
    },
  },
});
</script>
