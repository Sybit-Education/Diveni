<template>
  <div>
    <h5>
      <i>{{ $t("session.prepare.step.selection.mode.description.withJira.subtitle") }}</i>
    </h5>

    <ul>
      <li>
        <!-- <div class="form-check form-switch">
          <input
            id="flexSwitchCheckDefault"
            v-model="switch1"
            class="form-check-input"
            type="checkbox"
          />
          <label class="form-check-label" for="flexSwitchCheckDefault">{{
            $t("session.prepare.step.selection.mode.description.withJira.descriptionLine1")
          }}</label>
        </div> -->
        {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine1") }}
        <!-- <div v-if="!switch1"> -->

        <sign-in-with-jira-cloud-button-component
          v-if="isJiraCloudEnabled"
          class="my-1"
        ></sign-in-with-jira-cloud-button-component>
        <!-- </div> -->
        <!-- <div v-if="switch1"> -->
        <sign-in-with-jira-server-button-component
          v-if="isJiraServerEnabled"
          class="my-1"
        ></sign-in-with-jira-server-button-component>
        <!-- </div> -->
      </li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine2") }}
        <project-selection-component
          v-if="isLoggedInWithJira"
          class="my-1"
        ></project-selection-component>
      </li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine3") }}
      </li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine4") }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import constants from "@/constants";
import Vue from "vue";
import SignInWithJiraCloudButtonComponent from "./SignInWithJiraCloudButtonComponent.vue";
import SignInWithJiraServerButtonComponent from "./SignInWithJiraServerButtonComponent.vue";
import ProjectSelectionComponent from "./ProjectSelectionComponent.vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "JiraComponent",
  components: {
    SignInWithJiraCloudButtonComponent,
    SignInWithJiraServerButtonComponent,
    ProjectSelectionComponent,
  },

  data() {
    return {
      switch1: false,
      isJiraCloudEnabled: constants.isJiraCloudEnabled,
      isJiraServerEnabled: constants.isJiraServerEnabled,
    };
  },
  computed: {
    getTokenId() {
      return this.$store.state.tokenId;
    },
    isLoggedInWithJira() {
      return !!this.$store.state.tokenId;
    },
  },
  watch: {
    getTokenId: {
      handler(newVal) {
        console.log(newVal);
        if (newVal) {
          apiService.getAllProjects().then((pr) => {
            this.$store.commit("setProjects", pr);
          });
          this.$toast.success("Logged in");
        }
      },
      immediate: true,
    },
  },
});
</script>
