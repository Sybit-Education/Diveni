<template>
  <div>
    <b-button variant="primary" :disabled="disabled" @click="clicked = !clicked">
      {{
        t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithGitlab.label"
        )
      }}
    </b-button>
    <div v-if="clicked" class="my-2">
      <div class="inline-div input-div">
        <b-input
          v-model="patToken"
          class="patInputField"
          placeholder="Type in your PAT"
          type="password"
        >
        </b-input>
      </div>
      <div class="inline-div">
        <b-button variant="primary" :disabled="patToken === ''" @click="getAccessToken()"
          >Sign in
        </b-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import apiService from "@/services/api.service";
import { defineComponent } from "vue";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SignInWithGitlabButtonComponent",
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
  data() {
    return {
      clicked: false,
      patToken: "",
    };
  },
  methods: {
    async getAccessToken() {
      try {
        const response = await apiService.sendGitlabOauth2AuthorizationCode(this.patToken);
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

<style scoped>
.inline-div {
  display: inline-block;
}

.input-div {
  margin-right: 0.5vw;
}

.patInputField {
  width: 100%;
  margin-right: 5em;
}
</style>
