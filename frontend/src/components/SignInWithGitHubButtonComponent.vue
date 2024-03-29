<template>
  <div>
    <b-button variant="primary" :disabled="disabled" @click="clicked = !clicked">
      {{
        t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithGithub.label"
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
import { defineComponent } from "vue";
import apiService from "@/services/api.service";
import { useI18n } from "vue-i18n";
import { useToast } from "vue-toastification";
import { useDiveniStore } from "@/store";

export default defineComponent({
  name: "SignInWithGitHubButtonComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  setup() {
    const { t } = useI18n();
    const toast = useToast();
    const store = useDiveniStore();
    return { t, store, toast };
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
        const response = await apiService.sendGithubOauth2AuthorizaionCode(this.patToken);
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
