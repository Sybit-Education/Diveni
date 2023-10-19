<template>
  <b-container id="result-page">
    <h1 class="mx-2 centerItems">
      {{ $t("page.results.title") }}
    </h1>
    <div v-if="userStories.length === 0" class="text-center">
      {{ $t("page.results.noUserStories") }}
    </div>
    <b-list-group v-else id="results">
      <b-list-group-item v-for="(story, index) of userStories" :key="index" class="text-center stories">
        {{ story.title ? story.title : "No title ..." }}
        <b-button class="mx-2 estimationButtons" pill >
          {{ story.estimation ? story.estimation : "?" }}
        </b-button>
      </b-list-group-item>
    </b-list-group>
    <b-row class=" text-center centerItems">
      <b-col>
        <b-button
          id="downloadButton"
          :disabled="userStories.length === 0"
          @click="downloadUserStoriesAsCSV()"
        >
          {{ $t("page.results.button.download") }}
        </b-button>
        <b-button
          id="goBackHomeButton"
          class="mx-2"
          @click="goHome()"
        >
          {{ $t("page.results.button.home") }}
        </b-button>
      </b-col>
    </b-row>
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import papaparse from "papaparse";

export default Vue.extend({
  name: "SessionPage",
  components: {},
  computed: {
    userStories() {
      return this.$store.state.userStories;
    },
  },
  methods: {
    downloadUserStoriesAsCSV() {
      const fileType = "data:text/csv;charset=utf-8,";
      const csv = papaparse.unparse(this.userStories, {
        columns: ["title", "description", "estimation"],
        header: true,
        delimiter: ";",
      });
      const blob = new Blob([csv], { type: fileType });
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = "userStories.csv";
      link.click();
      URL.revokeObjectURL(link.href);
    },
    goHome() {
      this.$store.commit("setUserStories", { stories: [] });
      this.$store.commit("clearStore");
      this.$router.push({ name: "LandingPage" });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

.centerItems {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  min-height: 10vh;
}

.estimationButtons  {
  background-color: var(--startButton);
  color: var(--text-primary-color);
}

.estimationButtons:hover {
  background-color: var(--startButtonHovered);
  color: var(--text-primary-color);
}

.estimationButtons:focus{
  background-color: var(--startButtonHovered) !important;
  color: var(--text-primary-color);
}
.estimationButtons:active{
  background-color: var(--startButtonHovered) !important;
}

#downloadButton {
  background-color: var(--startButton);
  border-radius: var(--buttonShape);
  color: var(--text-primary-color);
}

#downloadButton:hover {
  background-color: var(--startButtonHovered);
  border-radius: var(--buttonShape);
  color: var(--text-primary-color);
}

#goBackHomeButton {
  background-color: var(--joinButton);
  border-radius: var(--buttonShape);
  color: var(--text-primary-color);
}

#goBackHomeButton:hover {
  background-color: var(--joinButtonHovered);
  color: var(--text-primary-color);
}

#results {
  border-radius: var(--element-size);
}

.stories {
  color: black;
}
</style>
