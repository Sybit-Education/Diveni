<template>
  <b-container>
    <h1 class="my-5 mx-2">
      {{ title }}
    </h1>
    <b-row class="justify-content-center">
      <landing-page-card
        class="col-md-4 m-2 col-12"
        :title="$t('general.meeting.new.title')"
        :description="$t('general.meeting.new.description')"
        :button-text="$t('general.meeting.new.startbutton')"
        :on-click="goToPrepareSessionPage"
      />
      <landing-page-card
        class="col-md-4 m-2 col-12"
        :title="$t('general.meeting.join.title')"
        :description="$t('general.meeting.join.description')"
        :button-text="$t('general.meeting.join.startbutton')"
        :on-click="goToJoinPage"
      />
      <landing-page-card
        v-if="sessionWrapper.session"
        class="col-md-4 m-2 col-12"
        :title="$t('general.meeting.reconnect.title')"
        :description="$t('general.meeting.reconnect.description')"
        :button-text="$t('general.meeting.reconnect.startbutton')"
        :on-click="goToSessionPage"
      />
    </b-row>
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import LandingPageCard from "../components/LandingPageCard.vue";
import Constants from "../constants";
import Session from "../model/Session";

export default Vue.extend({
  name: "LandingPage",
  components: {
    LandingPageCard,
  },
  data() {
    return {
      title: "Planning-Poker",
      sessionWrapper: {} as { session: Session },
      startNewSessionOnMounted: false,
    };
  },
  created() {
    this.checkAdminCookie();
  },
  methods: {
    async checkAdminCookie() {
      console.log("checking admin cookie");
      const cookie = window.localStorage.getItem("adminCookie");
      if (cookie !== null) {
        console.log(`Found admin cookie: '${cookie}'`);
        const url = Constants.backendURL + Constants.createSessionRoute;
        try {
          const session = (
            await this.axios.get(url, {
              params: {
                adminCookie: cookie,
              },
            })
          ).data as {
            sessionID: string;
            adminID: string;
            sessionConfig: {
              set: Array<string>;
              timerSeconds: number;
              userStories: Array<{
                title: string;
                description: string;
                estimation: string | null;
                isActive: false;
              }>;
            };
            sessionState: string;
          };
          this.sessionWrapper = { session };
          this.startNewSessionOnMounted = true;
        } catch (e) {
          console.clear();
          console.log(`got error: ${e}`);
          window.localStorage.removeItem("adminCookie");
        }
      }
    },
    goToJoinPage() {
      this.$router.push({ name: "JoinPage" });
    },
    goToPrepareSessionPage() {
      this.$router.push({ name: "PrepareSessionPage" });
    },
    goToSessionPage() {
      this.$store.commit("setUserStories", {
        stories: this.sessionWrapper.session.sessionConfig.userStories,
      });
      this.$router.push({
        name: "SessionPage",
        params: {
          sessionID: this.sessionWrapper.session.sessionID,
          adminID: this.sessionWrapper.session.adminID,
          voteSetJson: JSON.stringify(this.sessionWrapper.session.sessionConfig.set),
          sessionState: this.sessionWrapper.session.sessionState,
          timerSecondsString: this.sessionWrapper.session.sessionConfig.timerSeconds.toString(),
          startNewSessionOnMountedString: this.startNewSessionOnMounted.toString(),
        },
      });
    },
  },
});
</script>
