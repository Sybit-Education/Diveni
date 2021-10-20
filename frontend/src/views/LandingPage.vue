<template>
  <v-container>
    <div class="text-h3 ma-8">
      {{ title }}
    </div>
    <v-layout class="row wrap">
      <v-flex class="xs12 sm6">
        <landing-page-card
          :title="'Join meeting'"
          :description="'Join a existing meeting and help your team estimating'"
          :button-text="'GO'"
          :on-click="sendCreateSessionRequest"
        />
      </v-flex>
      <v-flex class="xs12 sm6">
        <landing-page-card
          :title="'New meeting'"
          :description="'Start a new meeting, invite colleagues and start estimating'"
          :button-text="'GO'"
        />
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script lang="ts">
import Vue from 'vue';
import LandingPageCard from '../components/LandingPageCard.vue';
import Session from '../model/Session';
import * as Constants from '../constants';

export default Vue.extend({
  name: 'LandingPage',
  components: {
    LandingPageCard,
  },
  data() {
    return {
      title: 'Agile poker',
    };
  },
  methods: {
    async sendCreateSessionRequest() {
      const url = Constants.default.backendURL + Constants.default.backendSessionRoute;
      try {
        const response = await this.$axios.get(url);
        const session = Session.fromJson(response);
        this.goToWaitingRoomPage(session);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    goToWaitingRoomPage(session: Session) {
      this.$router.push({ path: '/waitingRoom', arguments: { session } });
    },
  },
});
</script>
