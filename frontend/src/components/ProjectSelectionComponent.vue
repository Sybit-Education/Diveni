<template>
  <div ref="projectSelectionRef">
    <b-input-group>
      <b-input-group-prepend>
        <b-input-group-text><BIconSearch id="searchIcon"></BIconSearch></b-input-group-text>
      </b-input-group-prepend>
      <b-input
        id="search"
        v-model="input"
        type="text"
        :placeholder="
          t(
            'session.prepare.step.selection.mode.description.withIssueTracker.placeholder.searchProjects'
          )
        "
        @input="filterProjects()"
        @focus="
          showResult = true;
          filterProjects();
        "
      />
    </b-input-group>
    <ul v-if="showResult" class="vue-autocomplete-input-tag-items">
      <li
        v-for="name in projectNamesList"
        :key="name"
        class="vue-autocomplete-input-tag-item"
        @click="selectProject(name)"
      >
        {{ name }}
      </li>
    </ul>

    <div class="mt-3">
      {{ t("session.prepare.step.selection.mode.description.withIssueTracker.selectedProject") }}
      <strong>{{ aCorrectProject ? selected : "-" }}</strong>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import apiService from "@/services/api.service";
import Project from "../model/Project";
import { useDiveniStore } from "@/store";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "ProjectSelectionComponent",

  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    return { store, t };
  },
  data() {
    return {
      selected: "",
      projectArray: [] as Array<Project>,
      projectNamesList: [] as Array<string>,
      input: "",
      aCorrectProject: false,
      showResult: false,
    };
  },
  computed: {
    projects(): Array<Project> {
      return this.store.projects;
    },
  },
  mounted() {
    document.addEventListener("click", this.handleGlobalClick);
  },
  beforeUnmount() {
    document.removeEventListener("click", this.handleGlobalClick);
  },
  methods: {
    handleGlobalClick(event: MouseEvent) {
      const isOutsideComponent = !(this.$refs.projectSelectionRef as HTMLElement).contains(
        event.target as Node
      );
      if (isOutsideComponent) {
        this.showResult = false;
      }
    },
    async getUserStories() {
      this.aCorrectProject = false;
      const selectedProject = this.projects.find((p) => p.name === this.selected);
      if (selectedProject) {
        this.aCorrectProject = true;
        this.store.setSelectedProject(selectedProject);
        const response = await apiService.getUserStoriesFromProject(this.selected);
        this.store.setUserStories({ stories: response });
      }
      this.showResult = false;
    },
    filterProjects: function () {
      if (this.input === "") {
        this.projectNamesList = this.getProjectNames();
        return;
      }
      if (this.input !== "") {
        let filteredProjects: string[] = [];
        this.getProjectNames().forEach((name) => {
          if (name.toLowerCase().includes(this.input.toLowerCase())) {
            filteredProjects.push(name);
          }
        });
        this.projectNamesList = filteredProjects;
      }
    },
    getProjectNames(): Array<string> {
      return this.projects.map((p) => p.name);
    },
    selectProject(name: string) {
      this.projects.forEach((project) => {
        if (project.name === name) {
          this.selected = project.name;
        }
      });
      this.input = this.selected;
      this.getUserStories();
    },
  },
});
</script>

<style lang="scss">
.vue-autocomplete-input-tag-items {
  border: 1px solid #ccc;
  max-height: 200px;
  margin-top: 8px;
  width: 100%;
  background-color: var(--preparePageNotSelectedTabBackground);
  border-radius: 8px;
  overflow: auto;

  &:hover {
    background-color: var(--preparePageInActiveTabHover);
  }
}

.vue-autocomplete-input-tag-item {
  padding: 6px 16px;
  max-width: 100%;
  cursor: pointer;
  text-align: left;
  font-size: 16px;
}

.vue-autocomplete-input-tag-item:hover {
  background-color: var(--preparePageInActiveTabHover);
}
</style>
