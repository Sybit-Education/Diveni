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
        :on-click="() => {}"
      />
      <div class="col-12 col-md-1" />
      <b-col />
    </b-row>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import LandingPageCard from '../components/LandingPageCard.vue';
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
        const response = await this.axios.post(url);
        this.goToSessionPage(response.data as string);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    goToSessionPage(session: string) {
      this.$router.push({ path: '/session', params: { session } });
    },
  },
});
</script>
