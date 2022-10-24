<template>
  <b-container>
    <div class="d-flex justify-content-center align-items-center">
      <b-spinner variant="success"></b-spinner>
      <h1 class="m-5">{{ $t("general.pleaseWait") }}</h1>
    </div>
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useToast } from "vue-toastification";
import apiService from "@/services/api.service";

export default defineComponent({
  name: "JiraCallbackPage",
  created() {
    const urlSearchParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlSearchParams.entries());
    const jiraStateId = localStorage.getItem("jiraStateId");
    if (
      !params.code ||
      !params.state ||
      !jiraStateId ||
      jiraStateId !== params.state
    ) {
      useToast().error(
        this.$t("session.notification.messages.jiraLoginFailed")
      );
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
      this.$router.push({
        name: "PrepareSessionPage",
        query: { tabIndex: "2" },
      });
    },
    showToast(error) {
      if (error.message == "failed to retrieve access token") {
        useToast().error(
          this.$t("session.notification.messages.jiraCredentials")
        );
      } else {
        useToast().error(
          this.$t("session.notification.messages.jiraLoginFailed")
        );
      }
      console.error(error);
    },
  },
});
</script>

<style scoped></style>
