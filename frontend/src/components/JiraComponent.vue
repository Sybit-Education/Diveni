<template>
  <div>
    <h5>
      <i>{{
        $t("session.prepare.step.selection.mode.description.withJira.subtitle")
      }}</i>
    </h5>

    <ul>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withJira.descriptionLine1"
          )
        }}
        <sign-in-with-jira-cloud-button-component
          v-if="isJiraCloudEnabled"
          class="my-1"
        ></sign-in-with-jira-cloud-button-component>
        <sign-in-with-jira-server-button-component
          v-if="isJiraServerEnabled"
          class="my-1"
        ></sign-in-with-jira-server-button-component>
      </li>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withJira.descriptionLine2"
          )
        }}
        <project-selection-component
          v-if="isLoggedInWithJira"
          class="my-1"
        ></project-selection-component>
      </li>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withJira.descriptionLine3"
          )
        }}
      </li>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withJira.descriptionLine4"
          )
        }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import constants from "@/constants";
import { defineComponent } from "vue";
import SignInWithJiraCloudButtonComponent from "./SignInWithJiraCloudButtonComponent.vue";
import SignInWithJiraServerButtonComponent from "./SignInWithJiraServerButtonComponent.vue";
import ProjectSelectionComponent from "./ProjectSelectionComponent.vue";
import { useToast } from "vue-toastification";
import apiService from "@/services/api.service";

export default defineComponent({
  name: "JiraComponent",
  components: {
    SignInWithJiraCloudButtonComponent,
    SignInWithJiraServerButtonComponent,
    ProjectSelectionComponent,
  },

  data() {
    return {
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
        if (newVal) {
          apiService.getAllProjects().then((pr) => {
            this.$store.commit("setProjects", pr);
          });
          useToast().success("Logged in");
        }
      },
      immediate: true,
    },
  },
});
</script>
