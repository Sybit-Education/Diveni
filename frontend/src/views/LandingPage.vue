<template>
  <b-container>
    <h1 class="my-5 mx-2">
      {{ title }}
    </h1>
    <b-row class="justify-content-center">
      <landing-page-card
        class="col-md-4 m-2 col-12"
        :title="'New meeting'"
        :description="'Start a new meeting, invite colleagues and start estimating'"
        :button-text="'GO'"
        :on-click="sendCreateSessionRequest"
      />
      <landing-page-card
        class="col-md-4 m-2 col-12"
        :title="'Join meeting'"
        :description="'Join a existing meeting and help your team estimating'"
        :button-text="'GO'"
        :on-click="goToJoinPage"
      />
    </b-row>
  </b-container>
</template>

<script lang="ts">
import Vue from 'vue';
import LandingPageCard from '../components/LandingPageCard.vue';
import Session from '../model/Session';
import Constants from '../constants';

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
      const url = Constants.backendURL + Constants.createSessionRoute;
      try {
        const session : Session = (await this.axios.post(url)).data as {
          sessionID: string,
          adminID: string,
          membersID: string
        };
        this.goToSessionPage(session);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    goToSessionPage(session: Session) {
      this.$router.push({
        name: 'SessionPage',
        params: {
          sessionID: session.sessionID,
          adminID: session.adminID,
        },
      });
    },
    goToJoinPage() {
      this.$router.push({ name: 'JoinPage' });
    },
  },
});
</script>
