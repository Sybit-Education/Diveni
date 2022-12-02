<template>
  <b-container>
    <h1>{{ headerText }}</h1>
    <b-card-group deck>
      <b-card v-bind:title="$t('page.landing.meeting.analytics.allCreatedSessionsTitle')">
        <b-card-text>
          {{ allSessions }} {{ overAllSessions }} <br />
          {{ allAttendees }} {{ overAllAttendees }}
        </b-card-text>

      </b-card>
      <b-card v-bind:title="$t('page.landing.meeting.analytics.lastMonthTitle')">
        <b-card-text>
          {{ lastMonthSessions }} {{ overAllSessionsFromLastMonth }} <br />
          {{ lastMonthAttendees }} {{ overAllAttendeesFromLastMonth }}
        </b-card-text>
      </b-card>
      <b-card v-bind:title="$t('page.landing.meeting.analytics.activeTitle')">
        <b-card-text>
          {{ currentSessionsText }} {{ currentSessions }} <br />
          {{ currentAttendeesText }} {{ currentAttendees }}
        </b-card-text>
      </b-card>
    </b-card-group>
  </b-container>

</template>
<script lang="ts">
import Vue from "vue";
import Constants from "../constants";
export default Vue.extend({
  name: "AnalyticsDataComponent",
  components: {
  },
  data() {
    return {
      overAllSessions: 0,
      overAllAttendees: 0,
      overAllSessionsFromLastMonth: 0,
      overAllAttendeesFromLastMonth: 0,
      currentSessions: 0,
      currentAttendees: 0,
    };
  },
  created() {
    this.getAllDiveniData();
  },
  computed: {
    headerText() {
      return this.$t('page.landing.meeting.analytics.title');
    },
    allSessions() {
      return this.$t('page.landing.meeting.analytics.allCreatedSessionsNumber');
    },
    allAttendees() {
      return this.$t('page.landing.meeting.analytics.allCreatedSessionsNumberAttendees');
    },
    lastMonthSessions() {
      return this.$t('page.landing.meeting.analytics.lastMonthSessions');
    },
    lastMonthAttendees() {
      return this.$t('page.landing.meeting.analytics.lastMonthAttendees');
    },
    currentSessionsText() {
      return this.$t('page.landing.meeting.analytics.activeSessions');
    },
    currentAttendeesText() {
      return this.$t('page.landing.meeting.analytics.activeAttendees');
    }
  },
  methods: {

    async getAllDiveniData() {
      const url = Constants.backendURL + Constants.getDiveniAnalytics;

      try {
        const listOfData = (await this.axios.get(url)).data as {
          amountOfAttendees: number;
          amountOfSessions: number;
          amountofAttendeesLastMonth: number;
          amountOfSessionsLastMonth: number;
          amountOfAttendeesCurrently: number;
          amountOfSessionsCurrently: number;
        }
        this.overAllSessions = listOfData.amountOfSessions;
        this.overAllAttendees = listOfData.amountOfAttendees;
        this.overAllSessionsFromLastMonth = listOfData.amountOfSessionsLastMonth;
        this.overAllAttendeesFromLastMonth = listOfData.amountofAttendeesLastMonth;
        this.currentSessions = listOfData.amountOfSessionsCurrently;
        this.currentAttendees = listOfData.amountOfAttendeesCurrently;
      } catch (e) {
        console.error("Got this Error: " + e);
      }
    },
  },
});

</script>
<style>

</style>