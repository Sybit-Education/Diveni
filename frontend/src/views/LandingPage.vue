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
        :on-click="goToPrepareSessionPage"
      />
      <landing-page-card
        class="col-md-4 m-2 col-12"
        :title="'Join meeting'"
        :description="'Join an existing meeting and help your team estimate'"
        :button-text="'GO'"
        :on-click="goToJoinPage"
      />
      <landing-page-card
        v-if="sessionWrapper.session"
        class="col-md-4 m-2 col-12"
        :title="'Reconnect to meeting'"
        :description="'Reconnect to existing session as host'"
        :button-text="'GO'"
        :on-click="goToSessionPage"
      />
    </b-row>
  </b-container>
</template>

<script lang="ts">
import Vue from 'vue';
import LandingPageCard from '../components/LandingPageCard.vue';
import Constants from '../constants';
import Session from '../model/Session';

export default Vue.extend({
  name: 'LandingPage',
  components: {
    LandingPageCard,
  },
  data() {
    return {
      title: 'Agile poker',
      sessionWrapper: {} as { session: Session },
    };
  },
  created() {
    this.checkAdminCookie();
  },
  methods: {
    async checkAdminCookie() {
      console.log('checking admin cookie');
      const cookie = window.localStorage.getItem('adminCookie');
      if (cookie !== null) {
        console.log(`Found admin cookie: '${cookie}'`);
        const url = Constants.backendURL + Constants.createSessionRoute;
        try {
          const session = (await this.axios.get(url, {
            params: {
              adminCookie: cookie,
            },
          })).data as {
            sessionID: string,
            adminID: string,
            sessionConfig: {
              set: Array<string>,
              userStories: Array<{title:string, description:string, estimation:string|null, isActive: false }>,
            },
            sessionState: string,
          };
          this.sessionWrapper = { session };
        } catch (e) {
          console.log(`got error: ${e}`);
          window.localStorage.removeItem('adminCookie');
        }
      }
    },
    goToJoinPage() {
      this.$router.push({ name: 'JoinPage' });
    },
    goToPrepareSessionPage() {
      this.$router.push({ name: 'PrepareSessionPage' });
    },
    goToSessionPage() {
      this.$router.push({
        name: 'SessionPage',
        params: {
          sessionID: this.sessionWrapper.session.sessionID,
          adminID: this.sessionWrapper.session.adminID,
          voteSetJson: JSON.stringify(this.sessionWrapper.session.sessionConfig.set),
          sessionState: this.sessionWrapper.session.sessionState,
        },
      });
    },
  },
});
</script>
