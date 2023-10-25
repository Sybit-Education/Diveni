<template>
  <div>
    <autocomplete
      v-model="selected"
      :items="getProjectNames"
      :placeholder="
        $t(
          'session.prepare.step.selection.mode.description.withIssueTracker.placeholder.searchProjects'
        )
      "
      @input="getUserStories"
    />

    <div class="mt-3">
      {{ $t("session.prepare.step.selection.mode.description.withIssueTracker.selectedProject") }}
      <strong>{{ aCorrectProject ? selected : "-" }}</strong>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import apiService from "@/services/api.service";
import Project from "../model/Project";
import autocomplete from "vue2-autocomplete-input-tag";

export default Vue.extend({
  name: "ProjectSelectionComponent",

  components: {
    autocomplete,
  },

  data() {
    return {
      selected: null,
      projectArray: [] as Array<Project>,
      input: "",
      aCorrectProject: false,
    };
  },
  computed: {
    projects(): Array<Project> {
      return this.$store.state.projects;
    },

    getProjectNames(): Array<string> {
      let projectNames;
      projectNames = this.projects.map((p) => p.name);
      return projectNames;
    },
  },

  methods: {
    async getUserStories() {
      this.aCorrectProject = false;
      const selectedProject = this.projects.find((p) => p.name === this.selected);
      if (selectedProject) {
        this.aCorrectProject = true;
        console.log(`Selected: ${selectedProject}`);
        this.$store.commit("setSelectedProject", selectedProject);
        console.log(`Selected Project: ${this.selected}`);
        const response = await apiService.getUserStoriesFromProject(this.selected);
        this.$store.commit("setUserStories", { stories: response });
      }
    },
  },
});
</script>

<style>
input {
  width: 100%;
  border: 1px solid #ccc;
  color: #666;
  border-radius: 10px;
  outline: none;
  padding: 9px 14px;
  box-sizing: border-box;
  font-size: 14px;
}
.vue2-autocomplete-input-tag-items {
  border: 1px solid #ccc;
  max-height: 200px;
  margin-top: 8px;
  width: 100%;
  background-color: white;
  border-radius: 8px;
  overflow: auto;
}
.vue2-autocomplete-input-tag-item {
  padding: 6px 16px;
  color: #4a4a4a;
  max-width: 100%;
  cursor: pointer;
  text-align: left;
  font-size: 14px;
}
.vue2-autocomplete-input-tag-item:hover {
  background-color: #e8e8e8;
}
</style>
