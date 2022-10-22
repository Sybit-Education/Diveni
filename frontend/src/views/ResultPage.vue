<template>
  <b-container id="result-page">
    <h1 class="my-5 mx-2">
      {{ $t("page.results.title") }}
    </h1>
    <div v-if="userStories.length === 0" class="text-center">
      {{ $t("page.results.noUserStories") }}
    </div>
    <b-list-group v-else>
      <b-list-group-item
        v-for="(story, index) of userStories"
        :key="index"
        class="text-center"
      >
        {{ story.title ? story.title : "No title ..." }}
        <b-button class="mx-2" pill variant="info">
          {{ story.estimation ? story.estimation : "?" }}
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
          {{ $t("page.results.button.download") }}
        </b-button>
        <b-button variant="secondary" class="mx-2" @click="goHome()">
          {{ $t("page.results.button.home") }}
        </b-button>
      </b-col>
    </b-row>
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import papaparse from "papaparse";

export default defineComponent({
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
<style scoped></style>
