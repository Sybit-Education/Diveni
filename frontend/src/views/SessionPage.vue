<template>
  <div>
    <user-stories-sidebar
      :card-set="['1', '2', 'A', '4', '5', '6']"
      :show-estimations="planningStart"
      :initial-stories="userStories"
      @userStoriesChanged="onUserStoriesChanged($event)"
    />
    <span v-if="!planningStart">
      <h1 class="my-5 mx-2"> {{ titleWaiting }} </h1>
      <h4 id="popover-link" class="mt-4 mx-2">
        Share the code <b-link
          href=""
          @click="copyLinkToClipboard()"
        > {{ sessionID }}
        </b-link> with your team mates. <b-icon-unlock />
        <b-popover target="popover-link" triggers="hover" placement="top">
          <b-button class="mx-1" variant="success" @click="copyIdToClipboard()">Copy ID </b-button>
          <b-button class="mx-1" variant="success" @click="copyLinkToClipboard()"> Copy link </b-button>
        </b-popover>
      </h4>

      <b-container class="my-5">
        <b-row class="d-flex justify-content-center border rounded" style="min-height: 200px;">
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
              :disabled="!members || members.length < 1"
              @click="sendStartEstimationMessages"
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
      <b-container>
        <b-row>
          <b-col>
            <h1 class="mt-5 mb-3 mx-2">
              {{ titleEstimate }}
            </h1>
            <estimate-timer
              :timer-triggered="triggerTimer"
              :timer="timerCountdownNumber"
              :start-timer-on-component-creation="startTimerOnComponentCreation"
              :initial-timer="60"
            />
            <b-button
              variant="outline-dark"
              class="ml-5 pl-5"
              @click="sendRestartMessage"
            >
              <b-icon-arrow-clockwise /> New
            </b-button>
          </b-col>
          <b-col>
            <h4 class="session-link">
              <b-link href="" @click="copyLinkToClipboard()">
                {{ sessionID }}
                <b-popover target="popover-link" triggers="hover" placement="top">
                  <b-button class="mx-1" variant="success" @click="copyIdToClipboard()">Copy ID </b-button>
                  <b-button class="mx-1" variant="success" @click="copyLinkToClipboard()"> Copy link </b-button>
                </b-popover>
              </b-link>
            </h4>
          </b-col>
        </b-row>
      </b-container>
      <b-container
        class="my-5 border rounded"
      >
        <b-row
          class="d-flex justify-content-center pb-3"
        >
          <h4 class="pl-2 pt-2">{{ stageLabelReady }}</h4>
          <b-icon-three-dots
            v-if="membersEstimated.length === 0"
            animation="fade"
            class="my-5"
            font-scale="4"
          />
          <SessionMemberCard
            v-for="member of membersEstimated"
            :key="member.memberID"
            :color="member.hexColor"
            :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
            :alt-attribute="member.avatarAnimal"
            :name="member.name"
            :estimation="member.currentEstimation"
            :estimate-finished="estimateFinished"
            :highest="estimateHighest.memberID === member.memberID"
            :lowest="estimateLowest.memberID === member.memberID"
          />
        </b-row>
        <b-row v-if="!estimateFinished">
          <hr class="m-0">
          <h4 class="pl-2 pt-2">{{ stageLabelWaiting }}</h4>
        </b-row>
        <b-row
          ref="grid"
          class="d-flex justify-content-center pb-3"
        >
          <SessionMemberCard
            v-for="(member, index) of membersPending"
            :key="member.memberID"
            :style="{top: -1 * ((Math.trunc(index/grid))) * 200+'px', zIndex: -1*index}"
            :color="member.hexColor"
            :asset-name="backendAnimalToAssetName(member.avatarAnimal)"
            :alt-attribute="member.avatarAnimal"
            :name="member.name"
            :estimation="member.currentEstimation"
          />
        </b-row>
      </b-container>
    </span>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import Constants from '../constants';
import SessionMemberCircle from '../components/SessionMemberCircle.vue';
import Member from '../model/Member';
import SessionMemberCard from '../components/SessionMemberCard.vue';
import UserStoriesSidebar from '../components/UserStoriesSidebar.vue';
import EstimateTimer from '../components/EstimateTimer.vue';

export default Vue.extend({
  name: 'SessionPage',
  components: {
    SessionMemberCircle,
    SessionMemberCard,
    UserStoriesSidebar,
    EstimateTimer,
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
      grid: 5,
      planningStart: false,
      connectionEstablished: false,
      voteSet: [] as string[],
      timerCountdownNumber: 60,
      triggerTimer: 0,
      startTimerOnComponentCreation: true,
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
    estimateFinished(): boolean {
      return !this.members.map((elem) => elem.currentEstimation).includes(null);
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
    members() {
      this.updateNumberOfCardColumns();
    },
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
    window.addEventListener('resize', this.updateNumberOfCardColumns);
    window.addEventListener('beforeunload', this.sendUnregisterCommand);
  },
  destroyed() {
    window.removeEventListener('resize', this.updateNumberOfCardColumns);
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
      this.updateNumberOfCardColumns();
    },
    copyIdToClipboard() {
      navigator.clipboard.writeText(this.sessionID).then(() => {
        console.log('Copying to clipboard was successful!');
      }, (err) => {
        console.error('Could not copy text: ', err);
      });
    },
    copyLinkToClipboard() {
      navigator.clipboard.writeText(`${document.URL.toString().replace('session', 'join?sessionID=')}${this.sessionID}`).then(() => {
        console.log('Copying to clipboard was successful!');
      }, (err) => {
        console.error('Could not copy text: ', err);
      });
    },
    backendAnimalToAssetName(animal: string) {
      return Constants.avatarAnimalToAssetName(animal);
    },
    updateNumberOfCardColumns() {
      setTimeout(() => {
        const grid = Array.from((this.$refs.grid as HTMLElement).children);
        const baseOffset = (grid[0] as HTMLElement).offsetTop;
        const breakIndex = grid.findIndex((item) => (item as HTMLElement).offsetTop > baseOffset);
        this.grid = (breakIndex === -1 ? grid.length : breakIndex);
        window.scrollTo({ top: 0 });
      }, 30);
    },
    sendRestartMessage() {
      const endPoint = Constants.webSocketRestartPlanningRoute;
      this.$store.commit('sendViaBackendWS', { endPoint });
      this.updateNumberOfCardColumns();
      this.retriggerTimer();
    },
    goToLandingPage() {
      this.$router.push({ name: 'LandingPage' });
    },
    retriggerTimer() {
      this.triggerTimer = (this.triggerTimer + 1) % 5;
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.session-link {
  margin-top: 3.8rem!important;
  text-align: center;
}
@media (min-width: 341px) {
  .session-link {
    text-align: right;
  }
}
</style>
