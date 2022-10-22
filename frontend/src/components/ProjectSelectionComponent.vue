<template>
  <div>
    <b-form-select
      v-model="selected"
      class="form-select"
      :options="getAllProjects"
      @change="getUserStories"
    ></b-form-select>
    <div class="mt-3">
      {{
        $t(
          "session.prepare.step.selection.mode.description.withJira.selectedProject"
        )
      }}
      <strong>{{ selected }}</strong>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import apiService from "@/services/api.service";
import Project from "../model/Project";

export default defineComponent({
  name: "ProjectSelectionComponent",

  data() {
    return {
      selected: null,
      project: "",
    };
  },
  computed: {
    projects(): Array<Project> {
      return this.$store.state.projects;
    },
    getAllProjects(): Array<unknown> {
      const projectNames = this.projects.map((p) => p.name);
      if (projectNames.length < 1) {
        return [
          {
            value: null,
            text: this.$t(
              "session.prepare.step.selection.mode.description.withJira.formSelection"
            ),
          },
        ];
      } else {
        return [
          {
            value: null,
            text: this.$t(
              "session.prepare.step.selection.mode.description.withJira.formSelection"
            ),
          },
          ...projectNames,
        ];
      }
    },
  },

  methods: {
    async getUserStories(project) {
      console.log(`Trying to select ${project}`);
      const selectedProject = this.projects.find((p) => p.name === project);
      console.log(`Selected: ${selectedProject}`);
      this.$store.commit("setSelectedProject", selectedProject);
      const response = await apiService.getUserStoriesFromProject(project);
      this.$store.commit("setUserStories", { stories: response });
    },
  },
});
</script>
