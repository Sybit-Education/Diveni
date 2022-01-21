<template>
  <div>
    <b-button variant="success" @click="redirectToJira()">
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
import { v4 as uuidv4 } from "uuid";

export default Vue.extend({
  name: "SignInWithJiraCloudButtonComponent",
  methods: {
    async redirectToJira() {
      const stateId = uuidv4();
      localStorage.setItem("jiraStateId", stateId);
      const url = `https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=3AXRsL6luwgjJGg2AsBUOCd5SNS1Hctq&scope=read%3Ajira-user%20write%3Ajira-work%20read%3Ajira-work&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2F%23%2FjiraCallback&state=${stateId}&response_type=code&prompt=consent`;
      window.location.href = url;
    },
  },
});
</script>
