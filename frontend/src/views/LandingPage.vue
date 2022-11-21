<template>
  <div id="landing-page">
    <b-container fluid class="teaser my-5">
      <b-container>
        <b-jumbotron header="DIVENI" lead="Instant free and easy remote Planning Poker" />
      </b-container>
    </b-container>
    <b-container class="my-5">
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
    <b-container>
      <h1>Statistics of Diveni</h1>
      <b-card-group deck>
        <b-card title="Overall created Sessions">
          <b-card-text>
            Overall created Sessions: {{ overAllSessions }} <br />
            Overall Attendees: {{ overAllAttendees }}
          </b-card-text>
        </b-card>
        <b-card title="Created Sessions over the last Month">
          <b-card-text>
            Created Sessions over the last Month: {{ overAllSessionsFromLastMonth }} <br />
            Attendees over the last Month: {{ overAllAttendeesFromLastMonth }}
          </b-card-text>
        </b-card>
        <b-card title="Currently Active Sessions">
          <b-card-text>
            Currently Active Sessions: {{ currentSessions }} <br />
            Current Number of Attendees: {{ currentAttendees }}
          </b-card-text>
        </b-card>
      </b-card-group>
    </b-container>
    <b-container class="py-5">
      <h1>Remote Planning Poker using DIVENI</h1>
      <b-card-group deck>
        <b-card title="Set up Session">
          <b-card-text>
            Create a planning session and select your prefered voting set.
          </b-card-text>
          <b-card-text>
            You could import your user stories or connect JIRA to syncronize story points.
          </b-card-text>

          <b-card-sub-title> Connecting Atlassian Jira </b-card-sub-title>
          <b-card-text>
            DIVENI could connect to JIRA Server and Cloud to show user stories and update the voted
            results of your planning poker.
          </b-card-text>
        </b-card>
        <b-card title="Invite your Team">
          <b-card-text> Invite your team using QR-Code, invite link or code. </b-card-text>
          <b-card-text>
            Every user will be randomly assinged to a animal and is ready to estimate.
          </b-card-text>
          <b-card-text>
            If everybody is in the session, you could start estimation. Having defined a time limit
            this will be used as limit for voting time.
          </b-card-text>
        </b-card>

        <b-card title="Estimate User Stories">
          <b-card-text>
            Every member of team could use your defined set to vote the selected user story.
          </b-card-text>
          <b-card-text>
            If erverybody has voted, DIVENI shows results and randomly selects two voters having
            voted minimum / maximum to discuss their estimation.
          </b-card-text>
          <b-card-text> After discussion you could repeat estimation. </b-card-text>
        </b-card>
      </b-card-group>
      <h1 class="mt-5">About DIVENI</h1>
      <p>DIVENI was initially developed by students of HTWG Constance and is open source now.</p>
      <p>
        More information could be found in the
        <a href="https://docs.diveni.io/">documentation</a>.
      </p>
    </b-container>
  </div>
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
      overAllSessions: 0,
      overAllAttendees: 0,
      overAllSessionsFromLastMonth: 0,
      overAllAttendeesFromLastMonth: 0,
      currentSessions: 0,
      currentAttendees: 0,
    };
  },
  created() {
    this.checkDiveniData(Constants.createDiveniData);
    this.checkDiveniData(Constants.createDiveniDataFromLastMonth);
    this.checkDiveniData(Constants.createCurrentDiveniData);
    this.checkAdminCookie();
  },
  methods: {
    async checkDiveniData(urlData) {
      const url = Constants.backendURL + urlData;
      try {
        const diveniData = (await this.axios.get(url)).data as {
          amountOfAttendees: number;
          amountOfSessions: number;
        };
        if (urlData === Constants.createDiveniData) {
          this.overAllSessions = diveniData.amountOfSessions;
          this.overAllAttendees = diveniData.amountOfAttendees;
        } else if (urlData === Constants.createDiveniDataFromLastMonth) {
          this.overAllSessionsFromLastMonth = diveniData.amountOfSessions;
          this.overAllAttendeesFromLastMonth = diveniData.amountOfAttendees;
        } else if (urlData === Constants.createCurrentDiveniData) {
          this.currentSessions = diveniData.amountOfSessions;
          this.currentAttendees = diveniData.amountOfAttendees;
        }
      } catch (e) {
        console.error(`got error: ${e}`);
      }
    },

    async checkDiveniDataFromLastMonth() {
      const url = Constants.backendURL + Constants.createDiveniDataFromLastMonth;
      try {
        const diveniData = (await this.axios.get(url)).data as {
          amountOfAttendees: number;
          amountOfSessions: number;
        };
        this.overAllSessionsFromLastMonth = diveniData.amountOfSessions;
        this.overAllAttendeesFromLastMonth = diveniData.amountOfAttendees;
        console.log(
          `Sessions: ${this.overAllSessionsFromLastMonth} : Attendees: ${this.overAllAttendeesFromLastMonth}`
        );
      } catch (e) {
        console.error(`got error: ${e}`);
      }
    },

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
            members: Array<string>;
          };
          this.sessionWrapper = { session };
        } catch (e) {
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

<style scoped>
.teaser {
  background: linear-gradient(rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.5)),
    url("~@/assets/img/diveni-background.png");
  background-size: cover;
  background-repeat: no-repeat;
}
.jumbotron {
  background-color: rgba(255, 255, 255, 0.5);
}
</style>
