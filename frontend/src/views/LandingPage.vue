<template>
  <b-container id="landing-page">
    <b-card-group deck class="justify-content-center">
      <landing-page-card
        :title="$t('page.landing.meeting.new.title')"
        :description="$t('page.landing.meeting.new.description')"
        :button-text="$t('page.landing.meeting.new.buttons.start.label')"
        :on-click="goToPrepareSessionPage"
      />
      <landing-page-card
        :title="$t('page.landing.meeting.join.title')"
        :description="$t('page.landing.meeting.join.description')"
        :button-text="$t('page.landing.meeting.join.buttons.start.label')"
        :on-click="goToJoinPage"
      />
      <landing-page-card
        v-if="sessionWrapper.session"
        :title="$t('page.landing.meeting.reconnect.title')"
        :description="$t('page.landing.meeting.reconnect.description')"
        :button-text="$t('page.landing.meeting.reconnect.buttons.start.label')"
        :on-click="goToSessionPage"
      />
    </b-card-group>
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
      sessionWrapper: {} as { session: Session },
      startNewSessionOnMounted: false,
    };
  },
  created() {
    this.checkAdminCookie();
  },
  methods: {
    async checkAdminCookie() {
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
                jiraId: number | null;
                title: string;
                description: string;
                estimation: string | null;
                isActive: false;
              }>;
              userStoryMode: string;
            };
            sessionState: string;
          };
          this.sessionWrapper = { session };
        } catch (e) {
          console.clear();
          console.error(`got error: ${e}`);
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
          userStoryMode: this.sessionWrapper.session.sessionConfig.userStoryMode,
        },
      });
    },
  },
});
</script>
