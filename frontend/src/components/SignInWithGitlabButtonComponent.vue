<template>
  <div>
    <b-button
      id="button"
      :disabled="disabled"
      @click="
       clicked = !clicked
     "
    >
      {{
        $t(
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
        <b-button
          @click="getAccessToken()"
        >
          Sign in
        </b-button>
      </div>
    </div>
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
  data() {
    return {
      clicked: false,
      patToken: ""
    }
  },
  methods: {
    async getAccessToken() {
      try {
        const response = await apiService.sendGitlabOauth2AuthorizationCode(this.patToken);
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
