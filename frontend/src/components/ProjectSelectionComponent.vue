<template>
  <div>
    <div id="inputField">
      <input
        v-model="input"
        class="search"
        type="text"
        placeholder="Filter your Project"
        @input="getAllProjects"
      />
    </div>
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
import Project from "../model/Project";

export default Vue.extend({
  name: "ProjectSelectionComponent",

  data() {
    return {
      selected: null,
      project: "",
      projectArray: [] as Array<Project>,
      input: "",
    };
  },
  computed: {
    projects(): Array<Project> {
      return this.$store.state.projects;
    },
    getAllProjects(): Array<unknown> {
      let projectNames;
      if (this.input.length > 0) {
        projectNames = this.getFilteredProjects(this.projects).map((p) => p.name);
      } else {
        projectNames = this.projects.map((p) => p.name);
      }
      if (projectNames.length < 1) {
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
          ...projectNames,
        ];
      }
    },
  },

  methods: {
    getFilteredProjects(projects): Array<Project> {
      let returnArray: Array<Project>;
      returnArray = [];
      projects.forEach((project) => {
        if (project.name.toLowerCase().includes(this.input.toLowerCase())) {
          returnArray.push(project);
        }
      });
      return returnArray;
    },

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

<style scoped>
.search {
  background-image: url("@/assets/magnifying glass.png");
  background-position: 3px;
  background-repeat: no-repeat;
  background-size: 22px 25px;
  padding-left: 30px;
  border-color: black;
  overflow: auto;
}
</style>
