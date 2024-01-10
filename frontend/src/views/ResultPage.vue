<template>
  <b-container>
    <h1 class="mx-2 centerItems">
      {{ t("page.results.title") }}
    </h1>
    <div v-if="userStories.length === 0" class="text-center">
      {{ t("page.results.noUserStories") }}
    </div>
    <b-list-group v-else id="results">
      <b-list-group-item
        v-for="(story, index) of userStories"
        :key="index"
        class="text-center stories"
      >
        {{ story.title ? story.title : "No title ..." }}
        <b-button class="mx-2" pill>
          {{ story.estimation ? story.estimation : "?" }}
        </b-button>
      </b-list-group-item>
    </b-list-group>
    <b-row class="text-center centerItems">
      <b-col>
        <b-button
          variant="primary"
          :disabled="userStories.length === 0"
          @click="downloadUserStoriesAsCSV()"
        >
          {{ t("page.results.button.download") }}
        </b-button>
        <b-button variant="primary" class="mx-2" @click="goHome()">
          {{ t("page.results.button.home") }}
        </b-button>
      </b-col>
    </b-row>
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import papaparse from "papaparse";
import { useDiveniStore } from "@/store";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SessionPage",
  components: {},
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    return { store, t };
  },
  computed: {
    userStories() {
      return this.store.userStories;
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
      this.store.setUserStories({ stories: [] });
      this.store.clearStore();
      this.$router.push({ name: "LandingPage" });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.centerItems {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  min-height: 10vh;
}

#results {
  border-radius: var(--element-size);
}

.list-group-item {
  background-color: var(--preparePageNotSelectedTabBackground);
}

.stories {
  color: var(--text-primary-color);
}

.btn-secondary {
  pointer-events: none;
}
</style>
