<template>
  <div>
    <b-button
      id="button"
      :disabled="disabled"
      @click="
        redirectToJira();
        $event.target.blur();
      "
    >
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

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
#button {
  background-color: var(--preparePageMainColor);
  color: var(--text-primary-color);
}

#button:hover {
  background-color: var(--primary-button-hovered);
  color: var(--text-primary-color);
}
</style>
