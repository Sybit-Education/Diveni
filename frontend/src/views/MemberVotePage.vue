<template>
  <div>
    <b-container>
      <div>
        <h1 class="my-5 mx-2">
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
            v-for="number in numbers"
            :key="number"
            :ref="`memberCard${number}`"
            class="flicking-panel mx-2"
            :number="number"
            :hex-color="hexColor"
            :dragged="number == draggedNumber"
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
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import RoundedAvatar from '../components/RoundedAvatar.vue';
import MemberVoteCard from '../components/MemberVoteCard.vue';
import Constants from '../constants';

export default Vue.extend({
  name: 'MemberVotePage',
  components: {
    RoundedAvatar,
    MemberVoteCard,
  },
  props: {
    memberID: { type: String, default: undefined },
    name: { type: String, default: undefined },
    hexColor: { type: String, default: undefined },
    avatarAnimalAssetName: { type: String, default: undefined },
  },
  created() {
    window.addEventListener('beforeunload', this.sendUnregisterCommand);
  },
  data() {
    return {
      title: 'Estimate!',
      numbers: [1, 2, 3, 5, 8, 13, 21, 34],
      draggedNumber: null,
      waitingText: 'Waiting for Scrum master to start ...',
    };
  },
  computed: {
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
        this.draggedNumber = null;
      }
    },
  },
  methods: {
    onSendVote({ vote }) {
      this.draggedNumber = vote;
      const endPoint = `${Constants.webSocketVoteRoute}`;
      this.$store.commit('sendViaBackendWS', { endPoint, data: vote });
    },
    sendUnregisterCommand() {
      const endPoint = `${Constants.webSocketUnregisterMemberRoute}`;
      this.$store.commit('sendViaBackendWS', { endPoint, data: null });
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
