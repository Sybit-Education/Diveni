<template>
  <b-container>
    <h1>{{ headerText }}</h1>
    <b-card-group deck>
      <b-card class="statsCards">
        <b-card-title style="text-align: center">
          {{ $t("page.landing.meeting.analytics.allCreatedSessionsTitle") }}
        </b-card-title>
        <b-card-text>
          <h2 class="numbers">{{ tweenedOverAllSessions.toFixed(0) }}</h2>
          <div style="text-align: center">
            {{ allSessions }}
          </div>
          <br />
          <h2 class="numbers">{{ tweenedOverAllAttendees.toFixed(0) }}</h2>
          <div style="text-align: center">
            {{ allAttendees }}
          </div>
        </b-card-text>
      </b-card>
      <b-card class="statsCards">
        <b-card-title style="text-align: center">
          {{ $t("page.landing.meeting.analytics.activeTitle") }}
        </b-card-title>
        <b-card-text>
          <h2 class="numbers">
            {{ tweenedCurrentSessions.toFixed(0) }}
          </h2>
          <div style="text-align: center">
            {{ allSessions }}
          </div>
          <br />
          <h2 class="numbers">
            {{ tweendCurrentAttendees.toFixed(0) }}
          </h2>
          <div style="text-align: center">
            {{ allAttendees }}
          </div>
        </b-card-text>
      </b-card>
    </b-card-group>
    <br />
  </b-container>
</template>
<script lang="ts">
import Vue from "vue";
import gsap from "gsap";
import apiService from "@/services/api.service";
export default Vue.extend({
  name: "AnalyticsDataComponent",
  data() {
    return {
      loaded: false,
      overAllSessions: 0,
      overAllAttendees: 0,
      currentSessions: 0,
      currentAttendees: 0,
      tweenedOverAllSessions: 0,
      tweenedOverAllAttendees: 0,
      tweenedCurrentSessions: 0,
      tweendCurrentAttendees: 0,
    };
  },
  computed: {
    headerText() {
      return this.$t("page.landing.meeting.analytics.title");
    },
    allSessions() {
      return this.$t("page.landing.meeting.analytics.sessionText");
    },
    allAttendees() {
      return this.$t("page.landing.meeting.analytics.attendeesText");
    },
  },
  watch: {
    overAllSessions(n) {
      gsap.to(this, { duration: 1.25, tweenedOverAllSessions: Number(n) || 0 });
    },
    overAllAttendees(n) {
      gsap.to(this, { duration: 1.25, tweenedOverAllAttendees: Number(n) || 0 });
    },
    currentSessions(n) {
      gsap.to(this, { duration: 1.25, tweenedCurrentSessions: Number(n) || 0 });
    },
    currentAttendees(n) {
      gsap.to(this, { duration: 1.25, tweendCurrentAttendees: Number(n) || 0 });
    },
  },
  created() {
    this.getAllDiveniData();
  },
  methods: {
    async getAllDiveniData() {
      this.loaded = false;
      let response = apiService.getAllDiveniData();
      let allData = await response.then(function (result) {
        let returnArray: Array<number> = [];
        returnArray.push(result.amountOfSessions);
        returnArray.push(result.amountOfAttendees);
        returnArray.push(result.amountOfSessionsCurrently);
        returnArray.push(result.amountOfAttendeesCurrently);
        return returnArray;
      });
      this.overAllSessions = allData[0];
      this.overAllAttendees = allData[1];
      this.currentSessions = allData[2];
      this.currentAttendees = allData[3];
      this.loaded = true;
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.statsCards {
  border-radius: 0.5rem;
  background-color: rgba(200, 200, 200, 0.5);
}
.numbers {
  text-align: center;
  font-weight: bold;
}
</style>
