<template>
  <div>
    <h5>
      <i>{{ $t("session.prepare.step.selection.mode.description.withJira.subtitle") }}</i>
    </h5>
    <ul>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine1") }}
        <sign-in-with-jira-cloud-button-component
          v-if="isJiraCloudEnabled"
          class="my-1"
          :disabled="isLoggedInWithJira"
        ></sign-in-with-jira-cloud-button-component>
        <sign-in-with-jira-server-button-component
          v-if="isJiraServerEnabled"
          class="my-1"
          :disabled="isLoggedInWithJira"
        ></sign-in-with-jira-server-button-component>
      </li>
      <li>{{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine2") }}</li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine3") }}
      </li>
      <li>{{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine4") }}</li>
    </ul>
  </div>
</template>

<script lang="ts">
import constants from "@/constants";
import Vue from "vue";
import SignInWithJiraCloudButtonComponent from "./SignInWithJiraCloudButtonComponent.vue";
import SignInWithJiraServerButtonComponent from "./SignInWithJiraServerButtonComponent.vue";

export default Vue.extend({
  name: "JiraComponent",
  components: { SignInWithJiraCloudButtonComponent, SignInWithJiraServerButtonComponent },
  data() {
    return {
      isJiraCloudEnabled: constants.isJiraCloudEnabled,
      isJiraServerEnabled: constants.isJiraServerEnabled,
      isLoggedInWithJira: !!this.$store.state.tokenId,
    };
  },
});
</script>
