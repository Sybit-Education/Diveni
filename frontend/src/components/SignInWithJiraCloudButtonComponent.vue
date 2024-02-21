<template>
  <div>
    <b-button
      variant="primary"
      :disabled="disabled"
      @click="
        redirectToJira();
        $event.target.blur();
      "
    >
      {{
        t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithJiraCloud.label"
        )
      }}
    </b-button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { v4 as uuidv4 } from "uuid";
import apiService from "@/services/api.service";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SignInWithJiraCloudButtonComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  setup() {
    const { t } = useI18n();
    return { t };
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
<style lang="scss" scoped></style>
