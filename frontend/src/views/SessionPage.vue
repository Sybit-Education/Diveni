<template>
  <b-container>
    <user-stories-sidebar
      :card-set="voteSet"
      :show-estimations="planningStart"
      :initial-stories="userStories"
      @userStoriesChanged="onUserStoriesChanged($event)"
    />
    <b-row class="mt-5 mb-3">
      <b-col><h1> {{ planningStart ? titleEstimate : titleWaiting }} </h1></b-col>
      <b-col v-if="planningStart">
        <copy-session-id-popup :session-id="sessionID" />
      </b-col>
    </b-row>
    <div v-if="!planningStart">
      <b-row class="align-items-center">
        <copy-session-id-popup
          :text-before-session-i-d="'Share the code'"
          :session-id="sessionID"
          :text-after-session-i-d="'with your team mates'"
        />
      </b-row>
      <b-row class="mt-5">
        <h4 class="text-center">
          Waiting for members to join
        </h4>
        <b-icon-three-dots animation="fade" class="" font-scale="3" />
      </b-row>
      <b-row class=" d-flex justify-content-center">
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
        <b-col class="text-center">
          <b-button
            variant="success"
            :disabled="!members || members.length < 1"
            @click="sendStartEstimationMessages"
          >
            Start Planning
          </b-button>
        </b-col>
      </b-row>
    </div>
    <div v-else>
      <b-row class="d-flex justify-content-start pb-3">
        <b-col>
          <b-button variant="outline-dark" @click="sendRestartMessage">
            <b-icon-arrow-clockwise />
            New
          </b-button>
          <b-button variant="outline-dark" class="mx-1" @click="sendVotingFinishedMessage">
            <b-icon-bar-chart />
            Show result
          </b-button>
          <b-button v-b-modal.close-session-modal variant="danger" class="mx-2">
            <b-icon-x />
            Close session
          </b-button>
          <b-modal id="close-session-modal" title="Are you sure" @ok="closeSession">
            <p class="my-4">
              Closing this session removes you and all members.
              You can download the user stories thereafter.
            </p>
          </b-modal>
        </b-col>
        <b-col>
          <estimate-timer
            :timer-triggered="triggerTimer"
            :timer="timerCountdownNumber"
            :start-timer-on-component-creation="startTimerOnComponentCreation"
            :initial-timer="60"
          />
        </b-col>
      </b-row>
      <b-row v-if="membersPending.length > 0">
        <h4 class="d-inline">
          Waiting for {{ membersPending.length }} /
          {{ membersPending.length + membersEstimated.length }}
        </h4>
      </b-row>
      <b-row class="my-1 d-flex justify-content-center flex-wrap ">
        <rounded-avatar
          v-for="member of membersPending"
          :key="member.memberID"
          class="mx-2"
          :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
          :color="member.hexColor"
          :show-name="true"
          :name="member.name"
        />
      </b-row>
      <hr>
      <b-row v-if="membersEstimated.length > 0">
        <h4 class="d-inline">
          Estimating finished {{ membersEstimated.length }} /
          {{ membersPending.length + membersEstimated.length }}
        </h4>
      </b-row>
      <b-row class="my-1 d-flex justify-content-center flex-wrap ">
        <SessionMemberCard
          v-for="member of membersEstimated"
          :key="member.memberID"
          :color="member.hexColor"
          :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
          :name="member.name"
          :estimation="member.currentEstimation"
          :estimate-finished="estimateFinished"
          :highest="estimateHighest.memberID === member.memberID"
          :lowest="estimateLowest.memberID === member.memberID"
        />
      </b-row>
    </div>
  </b-container>
</template>

<script lang="ts">
import Vue from 'vue';
import Constants from '../constants';
import SessionMemberCircle from '../components/SessionMemberCircle.vue';
import Member from '../model/Member';
import SessionMemberCard from '../components/SessionMemberCard.vue';
import UserStoriesSidebar from '../components/UserStoriesSidebar.vue';
import EstimateTimer from '../components/EstimateTimer.vue';
import CopySessionIdPopup from '../components/CopySessionIdPopup.vue';
import RoundedAvatar from '../components/RoundedAvatar.vue';

export default Vue.extend({
  name: 'SessionPage',
  components: {
    SessionMemberCircle,
    SessionMemberCard,
    UserStoriesSidebar,
    EstimateTimer,
    CopySessionIdPopup,
    RoundedAvatar,
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
    voteSetJson: {
      type: String,
      default: undefined,
    },
  },
  data() {
    return {
      titleWaiting: 'Waiting for members ...',
      titleEstimate: 'Estimate!',
      stageLabelReady: 'Ready',
      stageLabelWaiting: 'Waiting room',
      planningStart: false,
      connectionEstablished: false,
      voteSet: [] as string[],
      timerCountdownNumber: 60,
      triggerTimer: 0,
      startTimerOnComponentCreation: true,
      estimateFinished: false,
    };
  },
  computed: {
    userStories() {
      return this.$store.state.userStories;
    },
    members() {
      return this.$store.state.members;
    },
    webSocketIsConnected() {
      return this.$store.state.webSocketConnected;
    },
    membersPending(): Member[] {
      return this.members.filter((member: Member) => member.currentEstimation === null);
    },
    membersEstimated(): Member[] {
      return this.members.filter((member: Member) => member.currentEstimation !== null);
    },
    estimateHighest(): Member {
      return this.membersEstimated.reduce((prev, current) => (
        this.voteSet.indexOf(prev.currentEstimation!) > this.voteSet.indexOf(current.currentEstimation!) ? prev : current
      ));
    },
    estimateLowest(): Member {
      return this.membersEstimated.reduce((prev, current) => (
        this.voteSet.indexOf(prev.currentEstimation!) < this.voteSet.indexOf(current.currentEstimation!) ? prev : current
      ));
    },
  },
  watch: {
    webSocketIsConnected(isConnected) {
      if (isConnected) {
        console.debug('SessionPage: member connected to websocket');
        this.registerAdminPrincipalOnBackend();
        this.subscribeWSMemberUpdated();
      }
    },
  },
  mounted() {
    if (this.sessionID === undefined || this.adminID === undefined) {
      this.goToLandingPage();
    }
    this.voteSet = JSON.parse(this.voteSetJson);
    this.connectToWebSocket();
  },
  created() {
    window.addEventListener('beforeunload', this.sendUnregisterCommand);
  },
  destroyed() {
    window.removeEventListener('beforeunload', this.sendUnregisterCommand);
  },
  methods: {
    onUserStoriesChanged($event) {
      this.$store.commit('setUserStories', { stories: $event });
      if (this.connectionEstablished) {
        const endPoint = `${Constants.webSocketAdminUpdatedUserStoriesRoute}`;
        this.$store.commit('sendViaBackendWS', { endPoint, data: JSON.stringify($event) });
      }
    },
    connectToWebSocket() {
      const url = `${Constants.backendURL}/connect?sessionID=${this.sessionID}&adminID=${this.adminID}`;
      this.$store.commit('connectToBackendWS', url);
      this.connectionEstablished = true;
    },
    registerAdminPrincipalOnBackend() {
      const endPoint = Constants.webSocketRegisterAdminUserRoute;
      this.$store.commit('sendViaBackendWS', { endPoint });
    },
    subscribeWSMemberUpdated() {
      this.$store.commit('subscribeOnBackendWSAdminUpdate');
    },
    sendUnregisterCommand() {
      const endPoint = `${Constants.webSocketUnregisterRoute}`;
      this.$store.commit('sendViaBackendWS', { endPoint, data: null });
    },
    sendStartEstimationMessages() {
      const endPoint = Constants.webSocketStartPlanningRoute;
      this.$store.commit('sendViaBackendWS', { endPoint });
      this.planningStart = true;
    },
    sendVotingFinishedMessage() {
      if (!this.estimateFinished) {
        const endPoint = Constants.webSocketVotingFinishedRoute;
        this.$store.commit('sendViaBackendWS', { endPoint });
        this.estimateFinished = true;
      }
    },
    backendAnimalToAssetName(animal: string) {
      return Constants.avatarAnimalToAssetName(animal);
    },
    closeSession() {
      this.sendUnregisterCommand();
      this.$router.push({ name: 'ResultPage' });
    },
    sendRestartMessage() {
      this.estimateFinished = false;
      const endPoint = Constants.webSocketRestartPlanningRoute;
      this.$store.commit('sendViaBackendWS', { endPoint });
      this.reTriggerTime();
    },
    goToLandingPage() {
      this.$router.push({ name: 'LandingPage' });
    },
    reTriggerTime() {
      this.triggerTimer = (this.triggerTimer + 1) % 5;
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
