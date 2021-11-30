<template>
  <b-container>
    <div>
      <user-stories-sidebar
        :card-set="voteSet"
        :show-estimations="true"
        :initial-stories="userStories"
        :show-edit-buttons="false"
      />
      <h1 class="my-5">
        {{ title }}
      </h1>
      <b-row class="justify-content-center">
        <rounded-avatar
          :color="hexColor"
          :asset-name="avatarAnimalAssetName"
          :show-name="true"
          :name="name"
        />
      </b-row>
    </div>
    <b-row
      v-if="isStartVoting"
      class="my-5"
    >
      <flicking
        id="flicking"
        :options="{ renderOnlyVisible: false,
                    horizontal:true,
                    align: 'center',
                    bound: false,
                    defaultIndex: 0,
                    deceleration: 0.0005 }"
      >
        <member-vote-card
          v-for="(voteOption, index) in voteSet"
          :key="voteOption"
          :ref="`memberCard${voteOption}`"
          class="flicking-panel mx-2"
          :vote-option="voteOption"
          :index="index"
          :hex-color="hexColor"
          :dragged="voteOption == draggedVote"
          @sentVote="onSendVote"
        />
      </flicking>
    </b-row>
    <b-row
      v-else
      class="my-5 text-center"
    >
      <b-icon-three-dots
        animation="fade"
        class="my-5"
        font-scale="4"
      />
      <h1>{{ waitingText }} </h1>
    </b-row>
  </b-container>
</template>

<script lang="ts">
import Vue from 'vue';
import RoundedAvatar from '../components/RoundedAvatar.vue';
import MemberVoteCard from '../components/MemberVoteCard.vue';
import Constants from '../constants';
import UserStoriesSidebar from '../components/UserStoriesSidebar.vue';

export default Vue.extend({
  name: 'MemberVotePage',
  components: {
    RoundedAvatar,
    MemberVoteCard,
    UserStoriesSidebar,
  },
  props: {
    memberID: { type: String, default: undefined },
    name: { type: String, default: undefined },
    hexColor: { type: String, default: undefined },
    avatarAnimalAssetName: { type: String, default: undefined },
    voteSetJson: { type: String, default: undefined },
  },
  data() {
    return {
      title: 'Estimate!',
      draggedVote: null,
      waitingText: 'Waiting for Host to start ...',
      voteSet: [] as string[],
    };
  },
  computed: {
    userStories() {
      return this.$store.state.userStories;
    },
    memberUpdates() {
      return this.$store.state.memberUpdates;
    },
    isStartVoting(): boolean {
      return this.memberUpdates.at(-1) === Constants.memberUpdateCommandStartVoting;
    },
  },
  watch: {
    memberUpdates(updates) {
      if (updates.at(-1) === Constants.memberUpdateCommandStartVoting) {
        this.draggedVote = null;
      } else if (updates.at(-1) === Constants.memberUpdateUpdatedUserStories) {
        //  TODO: Set the current user story, but backend currently not able to send it.
      } else if (updates.at(-1) === Constants.memberUpdateCloseSession) {
        this.goToJoinPage();
      }
    },
  },
  created() {
    window.addEventListener('beforeunload', this.sendUnregisterCommand);
  },
  mounted() {
    if (this.memberID === undefined || this.name === undefined
          || this.hexColor === undefined || this.avatarAnimalAssetName === undefined) {
      this.goToJoinPage();
    }
    this.voteSet = JSON.parse(this.voteSetJson);
  },
  methods: {
    onSendVote({ vote }) {
      this.draggedVote = vote;
      const endPoint = `${Constants.webSocketVoteRoute}`;
      this.$store.commit('sendViaBackendWS', { endPoint, data: vote });
    },
    sendUnregisterCommand() {
      const endPoint = `${Constants.webSocketUnregisterRoute}`;
      this.$store.commit('sendViaBackendWS', { endPoint, data: null });
    },
    goToJoinPage() {
      this.$router.push({ name: 'JoinPage' });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#flicking {
  /* overflow:visible;  Add when fix is clear how to stay responsiv*/
  width: 100%;
}
</style>
