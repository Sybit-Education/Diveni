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
import apiService from "@/services/api.service";
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
      let response = apiService.getAllDiveniData();
      let allData  = await response.then(function(result) {
        let returnArray: Array<number> = [];
        returnArray.push(result.amountOfSessions);
        returnArray.push(result.amountOfAttendees);
        returnArray.push(result.amountOfSessionsLastMonth);
        returnArray.push(result.amountofAttendeesLastMonth);
        returnArray.push(result.amountOfSessionsCurrently);
        returnArray.push(result.amountOfAttendeesCurrently);
        return returnArray;
      });

      this.overAllSessions = allData[0];
      this.overAllAttendees = allData[1];
      this.overAllSessionsFromLastMonth = allData[2];
      this.overAllAttendeesFromLastMonth = allData[3];
      this.currentSessions = allData[4];
      this.currentAttendees = allData[5];
    },
  },
});

</script>
<style>

</style>