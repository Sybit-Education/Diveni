<template>
  <div>
    <b-form-select v-model="selected" :options="projects" @change="getUserStories"></b-form-select>
    <div class="mt-3">
      {{ $t("session.prepare.step.selection.mode.description.withJira.selectedProject") }}
      <strong>{{ selected }}</strong>
    </div>
    <!-- <div v-if="userStories.length > 0" class="mt-3">
      <strong>
        {{ $t("session.prepare.step.selection.mode.description.withJira.allUserStories") }}</strong
      >
    </div> 
    <b-list-group>
      <b-list-group-item v-for="item of userStories" :key="item">{{ item }}</b-list-group-item>
    </b-list-group>-->
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
      projects: [
        {
          value: null,
          text: this.$t("session.prepare.step.selection.mode.description.withJira.formSelection"),
        },
      ],
      userStories: [] as any,
      project: "",
    };
  },
  computed: {
    getAllProjects() {
      return this.$store.state.projects;
    },
  },
  watch: {
    getAllProjects(projects) {
      for (const project of projects) {
        this.projects.push({ value: project, text: project });
      }
    },
  },
  methods: {
    async getUserStories(project) {
      const response = await apiService.getUserStoriesFromProject(project);
      this.$store.commit("setUserStories", { stories: response });
      for (const userstory of response) {
        this.userStories.push(userstory.title);
      }
    },
  },
});
</script>
