<template>
  <div>
    <b-button variant="success" :disabled="disabled" @click="redirectToJira()">
      {{
        $t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithJiraCloud.label"
        )
      }}
    </b-button>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { v4 as uuidv4 } from "uuid";
import constants from "@/constants";

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
      const url = `${constants.jiraCloudAuthorizeUrl}&state=${stateId}`;
      window.location.href = url;
    },
  },
});
</script>
