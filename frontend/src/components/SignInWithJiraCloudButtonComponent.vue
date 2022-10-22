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
import { defineComponent } from "vue";
import { v4 as uuidv4 } from "uuid";
import constants from "@/constants";

export default defineComponent({
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
