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
            :alt-attribute="member.avatarAnimal"
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
        @click="getNumberOfCardColumns"
      >
        <b-icon-arrow-clockwise /> New
      </b-button>
      <b-container
        class="my-5 border rounded"
        style="min-height: 200px;"
      >
        <b-row
          class="d-flex justify-content-center"
          style="min-height: 200px;"
        >
          <b-icon-three-dots
            v-if="membersEstimated.length === 0"
            animation="fade"
            class="my-5"
            font-scale="4"/>
          <SessionMemberCard
            v-for="member of membersEstimated"
            :key="member.memberID"
            class="m-4"
            :color="member.hexColor"
            :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
            :alt-attribute="member.avatarAnimal"
            :name="member.name"
            :estimation="member.currentEstimation"
            :estimate-finished="estimateFinished"
          />
        </b-row>
        <b-row
          class="d-flex justify-content-center"
          ref="grid"
        >
          <SessionMemberCard
            v-for="(member, index) of membersPending"
            :key="member.memberID"
            class="m-4"
            :style="{top: -1 * ((Math.trunc(index/grid))) * 200+'px', zIndex: -1*index}"
            :color="member.hexColor"
            :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
            :alt-attribute="member.avatarAnimal"
            :name="member.name"
            :estimation="member.currentEstimation"
            :estimate-finished="estimateFinished"
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
      members: [],
      planningStart: false,
    };
  },
  computed: {
    membersPending() {
      return this.members.filter((member) => member.currentEstimation === null);
    },
    membersEstimated() {
      return this.members.filter((member) => member.currentEstimation !== null);
    },
    estimateFinished() {
      return !this.members.map((elem) => elem.currentEstimation).includes(null);
    },
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
  created() {
    window.addEventListener('resize', this.getNumberOfCardColumns);
  },
  destroyed() {
    window.removeEventListener('resize', this.getNumberOfCardColumns);
  },
  methods: {
    sendStartPlanningMessage() {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send(Constants.default.webSocketStartPlanningRoute, '', {});
        console.log(this.membersSorted);
        this.planningStart = true;
        this.getNumberOfCardColumns();
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
          this.subscribeToWebSocketBackend();
          this.sendJoinAdminCommand();
        },
        (error) => {
          console.error(error);
          this.webSocketConnected = false;
        });
    },
    subscribeToWebSocketBackend() {
      this.stompClient.subscribe(
        Constants.default.webSocketMembersUpdatedRoute,
        (message: webStomp.Frame) => {
          this.members = JSON.parse(message.body);
        },
      );
    },
    getNumberOfCardColumns() {
      setTimeout(() => {
        const grid = Array.from((this.$refs.grid as HTMLElement).children);
        const baseOffset = (grid[0] as HTMLElement).offsetTop;
        const breakIndex = grid.findIndex((item) => (item as HTMLElement).offsetTop > baseOffset);
        this.grid = (breakIndex === -1 ? grid.length : breakIndex);
        window.scrollTo({ top: 0 });
      }, 0);
    },
  },
});
</script>
