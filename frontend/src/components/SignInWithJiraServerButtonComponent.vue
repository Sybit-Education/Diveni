<template>
  <div>
    <b-button
      variant="success"
      :disabled="disabled"
      @click="
        openSignInWithJiraTab();
        openModal();
      "
    >
      {{
        $t(
          "session.prepare.step.selection.mode.description.withJira.buttons.signInWithJiraServer.label"
        )
      }}
    </b-button>
    <b-modal
      id="modal-verification-code"
      ref="modal"
      title="Verification code"
      @show="resetModal"
      @hidden="resetModal"
      @ok="handleOk"
    >
      <p>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withJira.dialog.description"
          )
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
              $t(
                'session.prepare.step.selection.mode.description.withJira.inputs.verificationCode.placeholder'
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
import { defineComponent, nextTick } from "vue";
import { BModal } from "bootstrap-vue";
import { useToast } from "vue-toastification";
import apiService from "@/services/api.service";

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
    };
  },
  methods: {
    checkFormValidity() {
      const valid = (
        this.$refs.form as HTMLFormElement & { checkValidity: () => boolean }
      ).checkValidity();
      this.verificationCodeState = valid;
      return valid;
    },
    async openSignInWithJiraTab() {
      const tokenDto = await apiService.getJiraOauth1RequestToken();
      this.token = tokenDto.token;
      window.open(tokenDto.url, "_blank")?.focus();
    },
    openModal() {
      nextTick(() => {
        if (this.$refs?.["modal-verification-code"]) {
          const modal = this.$refs?.["modal-verification-code"] as BModal;
          modal.show();
        }
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
        this.$store.commit("setTokenId", response.tokenId);
      } catch (e) {
        this.showToast(e);
      }
      nextTick(() => {
        if (this.$refs?.["modal-verification-code"]) {
          const modal = this.$refs?.["modal-verification-code"] as BModal;
          modal.hide();
        }
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
