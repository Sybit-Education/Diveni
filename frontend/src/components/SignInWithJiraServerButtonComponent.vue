<template>
  <div>
    <b-button
      id="button"
      :disabled="disabled"
      @click="
        openSignInWithJiraTab();
        openModal();
        $event.target.blur();
      "
    >
      {{
        t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithJiraServer.label"
        )
      }}
    </b-button>
    <b-modal
      v-if="showVerificationModal"
      id="modal-verification-code"
      ref="modal"
      title="Verification code"
      @show="resetModal"
      @hidden="resetModal"
      @ok="handleOk"
    >
      <p id="description">
        {{
          t("session.prepare.step.selection.mode.description.withIssueTracker.dialog.description")
        }}
      </p>
      <form ref="form" @submit.stop.prevent="handleSubmit">
        <b-form-group
          label="Verification code"
          label-for="input-verification-code"
          invalid-feedback="Verification code is required"
          :state="verificationCodeState"
        >
          <b-form-input
            id="input-verification-code"
            v-model="verificationCode"
            required
            :placeholder="
              t(
                'session.prepare.step.selection.mode.description.withIssueTracker.inputs.verificationCode.placeholder'
              )
            "
            :state="verificationCodeState"
          />
        </b-form-group>
      </form>
    </b-modal>
  </div>
</template>

<script lang="ts">
// eslint-disable-next-line
import { defineComponent } from "vue";
import apiService from "@/services/api.service";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import * as Vue from "vue";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SignInWithJiraServerButtonComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  data() {
    return {
      token: "",
      verificationCode: "",
      verificationCodeState: false,
      showVerificationModal: false,
    };
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    return { store, toast, t };
  },
  methods: {
    checkFormValidity() {
      const valid = (this.$refs.form as any & { checkValidity: () => boolean }).checkValidity();
      this.verificationCodeState = valid;
      return valid;
    },
    async openSignInWithJiraTab() {
      const tokenDto = await apiService.getJiraOauth1RequestToken();
      this.token = tokenDto.token;
      window.open(tokenDto.url, "_blank")?.focus();
    },
    openModal() {
      this.$nextTick(() => {
        this.showVerificationModal = true;
      });
    },
    resetModal() {
      this.verificationCode = "";
      this.verificationCodeState = false;
    },
    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault();
      this.handleSubmit();
    },
    async handleSubmit() {
      const valid = this.checkFormValidity();
      if (!valid) {
        return;
      }
      try {
        const response = await apiService.sendJiraOauth1VerificationCode(
          this.verificationCode,
          this.token
        );
        localStorage.setItem("tokenId", response.tokenId);
        this.store.setTokenId(response.tokenId);
      } catch (e) {
        this.showToast(e);
      }
      this.$nextTick(() => {
        this.showVerificationModal = false;
      });
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
#description {
  color: black;
}

#button {
  background-color: var(--preparePageMainColor);
  color: var(--text-primary-color);
}

#button:hover {
  background-color: var(--primary-button-hovered);
  color: var(--text-primary-color);
}
</style>
