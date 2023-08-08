<template>
  <div id="textDescription">
    <h5>
      <i>{{ $t("session.prepare.step.selection.mode.description.withIssueTracker.subtitle") }}</i>
    </h5>

    <ul>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withIssueTracker.descriptionLine1") }}
        <sign-in-with-jira-cloud-button-component
          v-if="isJiraCloudEnabled"
          class="my-1" />
        <sign-in-with-jira-server-button-component
          v-if="isJiraServerEnabled"
          class="my-1" />
        <sign-in-with-azure-cloud-button-component
          v-if="isAzureDevOpsEnabled"
          class="my-1" />
      </li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withIssueTracker.descriptionLine2") }}
        <project-selection-component
          v-if="isLoggedInWithJira"
          class="my-1"
        ></project-selection-component>
      </li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withIssueTracker.descriptionLine3") }}
      </li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withIssueTracker.descriptionLine4") }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import SignInWithJiraCloudButtonComponent from "./SignInWithJiraCloudButtonComponent.vue";
import SignInWithJiraServerButtonComponent from "./SignInWithJiraServerButtonComponent.vue";
import SignInWithAzureCloudButtonComponent from "./SignInWithAzureDevOpsButtonComponent.vue";
import ProjectSelectionComponent from "./ProjectSelectionComponent.vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "JiraComponent",
  components: {
    SignInWithJiraCloudButtonComponent,
    SignInWithJiraServerButtonComponent,
    SignInWithAzureCloudButtonComponent,
    ProjectSelectionComponent,
  },

  data() {
    return {
      isJiraCloudEnabled: false,
      isJiraServerEnabled: false,
      isAzureDevOpsEnabled: false,
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
  mounted() {
    apiService.getIssueTrackerConfig().then((result) => {
      this.isJiraCloudEnabled = result.isJiraCloudEnabled === "true";
      this.isJiraServerEnabled = result.isJiraServerEnabled === "true";
      this.isAzureDevOpsEnabled = result.isAzureDevOpsEnabled === "true";
    });
  },
});
</script>
<style scoped>

#textDescription {
  color: var(--text-primary-color);
}

</style>
