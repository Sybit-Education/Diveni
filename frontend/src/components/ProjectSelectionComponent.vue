<template>
  <div>
    <b-form-select
      v-model="selected"
      class="form-select"
      :options="getAllProjects"
      @change="getUserStories"
    ></b-form-select>
    <div class="mt-3">
      {{ $t("session.prepare.step.selection.mode.description.withJira.selectedProject") }}
      <strong>{{ selected }}</strong>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "ProjectSelectionComponent",

  data() {
    return {
      selected: null,
      project: "",
    };
  },
  computed: {
    getAllProjects() {
      const projects = this.$store.state.projects;
      if (projects.length < 1) {
        return [
          {
            value: null,
            text: this.$t("session.prepare.step.selection.mode.description.withJira.formSelection"),
          },
        ];
      } else {
        return [
          {
            value: null,
            text: this.$t("session.prepare.step.selection.mode.description.withJira.formSelection"),
          },
          ...projects,
        ];
      }
    },
  },

  methods: {
    async getUserStories(project) {
      const response = await apiService.getUserStoriesFromProject(project);
      this.$store.commit("setUserStories", { stories: response });
    },
  },
});
</script>
