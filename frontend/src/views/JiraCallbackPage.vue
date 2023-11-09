<template>
  <b-container>
    <div class="d-flex justify-content-center align-items-center">
      <b-spinner variant="success"></b-spinner>
      <h1 class="m-5">{{ $t("general.pleaseWait") }}</h1>
    </div>
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "JiraCallbackPage",
  created() {
    const urlSearchParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlSearchParams.entries());
    const jiraStateId = localStorage.getItem("jiraStateId");
    if (!params.code || !params.state || !jiraStateId || jiraStateId !== params.state) {
      this.$toast.error(this.$t("session.notification.messages.issueTrackerLoginFailed"));
      this.$router.push({ name: "PrepareSessionPage" });
      return;
    }
    this.verifyCode(params.code);
  },
  methods: {
    async verifyCode(code: string) {
      try {
        const response = await apiService.sendJiraOauth2AuthorizationCode(code);
        localStorage.setItem("tokenId", response.tokenId);
        this.$store.commit("setTokenId", response.tokenId);
      } catch (e) {
        this.showToast(e);
      }
      this.$router.push({ name: "PrepareSessionPage", query: { tabIndex: "2" } });
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

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped></style>
