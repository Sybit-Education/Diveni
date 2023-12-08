<template>
  <div>
    <b-button
      variant="primary"
      @click="
        openSignInWithJiraTab();
        openModal();
      "
    >
      {{
        t(
          "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithJira.label"
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
import { defineComponent } from "vue";
import apiService from "@/services/api.service";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SignInWithJiraButtonComponent",
  setup() {
    const { t } = useI18n();
    return { t };
  },
  data() {
    return {
      token: "",
      verificationCode: "",
      verificationCodeState: false,
      showVerificationModal: false,
    };
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
      await apiService.sendJiraOauth1VerificationCode(this.verificationCode, this.token);
      this.$nextTick(() => {
        this.showVerificationModal = false;
      });
    },
  },
});
</script>
