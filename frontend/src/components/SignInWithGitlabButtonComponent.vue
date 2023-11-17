<template>
  <div>
    <b-button
      id="button"
      :disabled="disabled"
      @click="
       getAccessToken()
     "
    >
      {{
        $t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithGitlab.label"
        )
      }}
    </b-button>
  </div>

</template>

<script lang="ts">
import apiService from "@/services/api.service";
import Vue from "vue";

export default Vue.extend({
  name: "SignInWithGitlabButtonComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    }
  },
  methods: {
    async getAccessToken() {
      try {
        const response = await apiService.sendGitlabOauth2AuthorizationCode();
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

<style scoped>
#button {
  background-color: var(--preparePageMainColor);
  color: var(--text-primary-color);
}

#button:hover {
  background-color: var(--primary-button-hovered);
  color: var(--text-primary-color);
}
</style>
