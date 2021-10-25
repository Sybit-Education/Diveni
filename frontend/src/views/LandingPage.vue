<template>
  <div>
    <h1 class="my-5 mx-2">
      {{ title }}
    </h1>
    <b-row>
      <div class="col-12 col-md-1" />
      <landing-page-card
        class="col-12 col-md-5 m-2"
        :title="'New meeting'"
        :description="'Start a new meeting, invite colleagues and start estimating'"
        :button-text="'GO'"
        :on-click="sendCreateSessionRequest"
      />
      <landing-page-card
        class="col-12  col-md-5 m-2"
        :title="'Join meeting'"
        :description="'Join a existing meeting and help your team estimating'"
        :button-text="'GO'"
        :on-click="goToJoinPage"
      />
      <div class="col-12 col-md-1" />
      <b-col />
    </b-row>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import LandingPageCard from '../components/LandingPageCard.vue';
import Session from '../model/Session';
import * as Constants from '../constants';
import UUID from '../model/UUID';

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
      const url = Constants.default.backendURL + Constants.default.createSessionRoute;
      try {
        const response = (await this.axios.post(url)).data as {
          sessionID: string,
          adminID: string,
          membersID: string
        };
        const session: Session = new Session(
          UUID.fromString(response.sessionID) as UUID,
          UUID.fromString(response.adminID) as UUID,
          UUID.fromString(response.membersID) as UUID,
        );
        this.goToSessionPage(session);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    goToSessionPage(session: Session) {
      this.$router.push({
        name: 'SessionPage',
        params: {
          sessionID: session.sessionID.value,
          adminID: session.adminID.value,
        },
      });
    },
    goToJoinPage() {
      this.$router.push({ name: 'JoinPage' });
    },
  },
});
</script>
