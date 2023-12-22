<template>
  <div id="textDescription">
    <h5>
      <i>{{ t("session.prepare.step.selection.mode.description.withIssueTracker.subtitle") }}</i>
    </h5>

    <ul>
      <li>
        {{ t("session.prepare.step.selection.mode.description.withIssueTracker.descriptionLine1") }}
        <sign-in-with-jira-cloud-button-component v-if="isJiraCloudEnabled" class="my-1" />
        <sign-in-with-jira-server-button-component v-if="isJiraServerEnabled" class="my-1" />
        <sign-in-with-azure-cloud-button-component v-if="isAzureDevOpsEnabled" class="my-1" />
        <sign-in-with-git-hub-button-component v-if="isGithubEnabled" class="my-1" />
      </li>
      <li>
        {{ t("session.prepare.step.selection.mode.description.withIssueTracker.descriptionLine2") }}
        <project-selection-component
          v-if="isLoggedInWithJira"
          class="my-1"
        ></project-selection-component>
      </li>
      <li>
        {{ t("session.prepare.step.selection.mode.description.withIssueTracker.descriptionLine3") }}
      </li>
      <li>
        {{ t("session.prepare.step.selection.mode.description.withIssueTracker.descriptionLine4") }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import SignInWithJiraCloudButtonComponent from "./SignInWithJiraCloudButtonComponent.vue";
import SignInWithJiraServerButtonComponent from "./SignInWithJiraServerButtonComponent.vue";
import SignInWithAzureCloudButtonComponent from "./SignInWithAzureDevOpsButtonComponent.vue";
import SignInWithGitHubButtonComponent from "@/components/SignInWithGitHubButtonComponent.vue";
import ProjectSelectionComponent from "./ProjectSelectionComponent.vue";
import apiService from "@/services/api.service";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "JiraComponent",
  components: {
    SignInWithJiraCloudButtonComponent,
    SignInWithJiraServerButtonComponent,
    SignInWithAzureCloudButtonComponent,
    SignInWithGitHubButtonComponent,
    ProjectSelectionComponent,
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    return { store, toast, t };
  },
  data() {
    return {
      isJiraCloudEnabled: false,
      isJiraServerEnabled: false,
      isAzureDevOpsEnabled: false,
      isGithubEnabled: false,
    };
  },
  computed: {
    getTokenId() {
      return this.store.tokenId;
    },
    isLoggedInWithJira() {
      return !!this.store.tokenId;
    },
  },
  watch: {
    getTokenId: {
      handler(newVal) {
        if (newVal) {
          apiService.getAllProjects().then((pr) => {
            this.store.setProjects(pr);
          });
          this.toast.success("Logged in");
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
      this.isGithubEnabled = result.isGithubEnabled === "true";
    });
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped></style>
