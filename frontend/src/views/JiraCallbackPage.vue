<template>
  <b-container>
    <div class="d-flex justify-content-center align-items-center">
      <b-spinner variant="success"></b-spinner>
      <h1 class="m-5">{{ t("general.pleaseWait") }}</h1>
    </div>
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import apiService from "@/services/api.service";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "JiraCallbackPage",
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    const router = useRouter();
    return { store, toast, t, router };
  },
  created() {
    const urlSearchParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlSearchParams.entries());
    const jiraStateId = localStorage.getItem("jiraStateId");
    if (!params.code || !params.state || !jiraStateId || jiraStateId !== params.state) {
      this.toast.error(this.t("session.notification.messages.issueTrackerLoginFailed"));
      this.router.push({ name: "PrepareSessionPage" });
      return;
    }
    this.verifyCode(params.code);
  },
  methods: {
    async verifyCode(code: string) {
      try {
        const response = await apiService.sendJiraOauth2AuthorizationCode(code);
        localStorage.setItem("tokenId", response.tokenId);
        this.store.setTokenId(response.tokenId);
      } catch (e) {
        this.showToast(e);
      }
      this.router.push({ name: "PrepareSessionPage", query: { tabIndex: "2" } });
    },
    showToast(error) {
      if (error.message == "failed to retrieve access token") {
        this.toast.error(this.t("session.notification.messages.issueTrackerCredentials"));
      } else {
        this.toast.error(this.t("session.notification.messages.issueTrackerLoginFailed"));
      }
      console.error(error);
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped></style>
