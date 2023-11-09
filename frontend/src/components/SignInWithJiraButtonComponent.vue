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
        $t(
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
          $t("session.prepare.step.selection.mode.description.withIssueTracker.dialog.description")
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
import Vue from "vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "SignInWithJiraButtonComponent",
  data() {
    return {
      token: "",
      verificationCode: "",
      verificationCodeState: false,
    };
  },
  methods: {
    checkFormValidity() {
      const valid = (this.$refs.form as Vue & { checkValidity: () => boolean }).checkValidity();
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
        this.$bvModal.show("modal-verification-code");
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
        this.$bvModal.hide("modal-verification-code");
      });
    },
  },
});
</script>
