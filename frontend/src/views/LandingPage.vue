<template>
  <v-container>
    <div class="text-h3 ma-8">
      {{ title }}
    </div>
    <v-layout class="row wrap">
      <v-flex class="xs12 sm6">
        <landing-page-card
          :title="'Join meeting'"
          :description="'Join a existing meeting and help your team estimating'"
          :button-text="'GO'"
        />
      </v-flex>
      <v-flex class="xs12 sm6">
        <landing-page-card
          :title="'New meeting'"
          :description="'Start a new meeting, invite colleagues and start estimating'"
          :button-text="'GO'"
        />
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script lang="ts">
import Vue from 'vue';
import LandingPageCard from '../components/LandingPageCard.vue';

export default Vue.extend({
  name: 'LandingPage',
  components: {
    // eslint-disable-next-line vue/no-unused-components
    LandingPageCard,
  },
  data() {
    return {
      title: 'Agile poker',
    };
  },
});
</script>
