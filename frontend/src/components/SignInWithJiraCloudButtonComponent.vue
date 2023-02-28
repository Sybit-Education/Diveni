<template>
  <div>
    <b-button variant="success" :disabled="disabled" @click="redirectToJira()">
      {{
        $t(
          "session.prepare.step.selection.mode.description.withJira.buttons.signInWithJiraCloud.label"
        )
      }}
    </b-button>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import {v4 as uuidv4} from "uuid";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "SignInWithJiraCloudButtonComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  methods: {
    async redirectToJira() {
      const stateId = uuidv4();
      localStorage.setItem("jiraStateId", stateId);
      apiService.getIssueTrackerConfig().then((result) => {
        window.location.href = `${result.jiraCloudAuthorizeUrl}&state=${stateId}`;
      });
    },
  },
});
</script>
