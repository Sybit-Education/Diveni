<template>
  <div>
    <b-button
      id="button"
      :disabled="disabled"
      @click="
        signIn();
        $event.target.blur();
      "
    >
      {{
        t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithAzureDevOps.label"
        )
      }}
    </b-button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import apiService from "@/services/api.service";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SignInWithAzureDevOpsButtonComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    return { store, toast, t };
  },
  methods: {
    async signIn() {
      try {
        const response = await apiService.sendAzureOauth2AuthorizationCode();
        localStorage.setItem("tokenId", response.tokenId);
        this.store.setTokenId(response.tokenId);
      } catch (e) {
        this.showToast(e);
      }
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
