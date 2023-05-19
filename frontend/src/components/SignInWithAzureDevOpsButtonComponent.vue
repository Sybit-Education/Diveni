<template>
  <div>
    <b-button variant="success" :disabled="disabled" @click="signIn()">
      {{
        $t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithAzureDevOps.label"
        )
      }}
    </b-button>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "SignInWithAzureDevOpsButtonComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  methods: {
    async signIn() {
      try {
        const response = await apiService.sendAzureOauth2AuthorizationCode();
        localStorage.setItem("tokenId", response.tokenId);
        this.$store.commit("setTokenId", response.tokenId);
      } catch (e) {
        this.showToast(e);
      }
    },
    showToast(error) {
      if (error.message == "failed to retrieve access token") {
        this.$toast.error(this.$t("session.notification.messages.issueTrackerCredentials"));
      } else {
        this.$toast.error(this.$t("session.notification.messages.issueTrackerLoginFailed"));
      }
      console.error(error);
    },
  },
});
</script>
