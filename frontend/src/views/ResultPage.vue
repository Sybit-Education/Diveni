<template>
  <b-container>
    <h1 class="my-5 mx-2">
      {{ titleResult }}
    </h1>
    <div v-if="userStories.length === 0" class="text-center">
      No user stories defined ...
    </div>
    <b-list-group v-else>
      <b-list-group-item v-for="(story, index) of userStories" :key="index" class="text-center">
        {{ story.title ? story.title : 'No title ...' }}
        <b-button
          class="mx-2"
          pill variant="info"
        >
          {{ story.estimation ? story.estimation : '?' }}
        </b-button>
      </b-list-group-item>
    </b-list-group>
    <b-row class="my-5 text-center">
      <b-col>
        <b-button
          variant="success"
          :disabled="userStories.length === 0"
          @click="downloadUserStoriesAsCSV()"
        >
          Download as CSV
        </b-button>
        <b-button variant="secondary" class="mx-2" @click="goHome()">
          Go Home
        </b-button>
      </b-col>
    </b-row>
  </b-container>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'SessionPage',
  components: {},
  data() {
    return {
      titleResult: 'Results',
    };
  },
  computed: {
    userStories() {
      return this.$store.state.userStories;
    },
  },
  methods: {
    downloadUserStoriesAsCSV() {
      const fileType = 'data:text/csv;charset=utf-8,';
      const header = 'Title;Description;Estimation\n';
      const csv = this.userStories.map((e) => `${e.title};${e.description};${e.estimation ? e.estimation : '?'}`).join('\n');
      const blob = new Blob([header + csv], { type: fileType });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'userStories.csv';
      link.click();
      URL.revokeObjectURL(link.href);
    },
    goHome() {
      this.$store.commit('setUserStories', { stories: [] });
      this.$store.commit('clearStore');
      this.$router.push({ name: 'LandingPage' });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
