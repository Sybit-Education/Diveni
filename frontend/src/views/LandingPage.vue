<template>
  <div class="pa-16">
    <div class="text-h3">{{ title }}</div>
    <v-container class="fill-height landing-page px-16">
      <v-row align="center" justify="center">
        <landing-page-card
          :title="'New meeting'"
          :description="'Start a new meeting, invite colleagues and start estimating'"
          :buttonText="'GO'"
        >
        </landing-page-card>
        <landing-page-card
          :title="'Join meeting'"
          :description="'Join a existing meeting and help your team estimating'"
          :buttonText="'GO'"
        >
        </landing-page-card>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import LandingPageCard from "../components/LandingPageCard.vue";

export default Vue.extend({
  name: "LandingPage",
  data() {
    return {
      title: "Agile poker",
    };
  },
  components: {
    LandingPageCard,
  },
});
</script>
