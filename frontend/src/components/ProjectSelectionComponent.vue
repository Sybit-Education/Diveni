<template>
  <div>
    <b-button
      variant="success"
      :disabled="disabled"
      @click="
        getProjects();
        openModal();
      "
    >
      Project selection
    </b-button>
    <b-modal id="modal-project-selection" ref="modal" title="Project Selection" @ok="handleOk">
      <p>Select your Project</p>

      <section v-if="projects" class="drop-down">
        <b-dropdown text="Projects" class="m-md-2">
          <b-dropdown-item v-for="(item, key) in projects" :key="key" @click="project = item.name">
            {{ item.name }}
          </b-dropdown-item>
        </b-dropdown>
      </section>
    </b-modal>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "ProjectSelectionComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  data() {
    return {
      item: "",
      //   projects: [{ name: "test" }, { name: "test1" }, { name: "test2" }],
      projects: [],
      project: "",
    };
  },
  methods: {
    async getProjects() {
      //   this.projects = await apiService.getAllProjects();
    },
    openModal() {
      this.$nextTick(() => {
        this.$bvModal.show("modal-project-selection");
      });
    },

    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault();
      this.handleSubmit(this.project);
    },
    async handleSubmit(project) {
      const response = await apiService.getUserStoriesFromProject(project);
      this.$store.commit("setProject", response.project);
      this.$nextTick(() => {
        this.$bvModal.hide("modal-project-selection");
      });
    },
  },
});
</script>
