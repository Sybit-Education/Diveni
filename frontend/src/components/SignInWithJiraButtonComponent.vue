<template>
  <div>
    <div v-if="enableJiraCloud">
      <b-button variant="success" class="my-1" @click="showJiraCloudCollapse = !showJiraCloudCollapse">
        {{
          $t(
            "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithJiraCloud.label"
          )
        }}
      </b-button>
      <b-collapse id="collapse-cloud" v-model="showJiraCloudCollapse">
        <form ref="jiraUrlForm" @submit.stop.prevent="handleJiraUrlSubmit">
          <b-form-group
            label="Your JIRA URL (ex: example.atlassian.net)"
            label-for="input-jira-url"
            invalid-feedback="JIRA Url is required"
            :state="jiraUrlState"
          >
            <b-input-group>
              <b-form-input
                id="input-jira-url"
                v-model="jiraUrlInput"
                required
                :placeholder="
                  $t(
                    'session.prepare.step.selection.mode.description.withIssueTracker.inputs.jiraUrl.placeholder'
                  )
                "
                :state="jiraUrlState"
              />
              <b-input-group-append>
                <b-button variant="success" @click="handleJiraUrlSubmit">Connect</b-button>
              </b-input-group-append>
            </b-input-group>
          </b-form-group>
        </form>
      </b-collapse>
    </div>
    <div v-if="enableJiraServer">
      <b-button variant="success" @click="openSignInWithJira('server')">
        {{
          $t(
            "session.prepare.step.selection.mode.description.withIssueTracker.buttons.signInWithJiraServer.label"
          )
        }}
      </b-button>
    </div>
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
// eslint-disable-next-line
import Vue from "vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "SignInWithJiraButtonComponent",
  props: {
    enableJiraCloud: {
      type: Boolean,
      required: false,
      default: false,
    },
    enableJiraServer: {
      type: Boolean,
      required: false,
      default: false,
    },
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  data() {
    return {
      showJiraCloudCollapse: false,
      selectedIssueTracker: "cloud",
      jiraUrlInput: "",
      jiraUrl: "",
      jiraUrlState: false,
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
    handleJiraUrlSubmit() {
      const valid = (
        this.$refs.jiraUrlForm as Vue & { checkValidity: () => boolean }
      ).checkValidity();
      this.jiraUrlState = valid;
      if (valid) {
        this.jiraUrl = this.jiraUrlInput;
        this.openSignInWithJira("cloud");
        this.showJiraCloudCollapse = false;
      }
    },
    async openSignInWithJira(type) {
      this.selectedIssueTracker = type;
      const tokenDto =
        this.selectedIssueTracker === "cloud"
          ? await apiService.getJiraCloudRequestToken("", this.jiraUrl)
          : await apiService.getJiraServerRequestToken();
      this.token = tokenDto.token;
      window.open(tokenDto.url, "_blank")?.focus();
      this.openModal();
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
      try {
        const response =
          this.selectedIssueTracker === "cloud"
            ? await apiService.sendJiraCloudVerificationCode(
                this.verificationCode,
                this.token,
                this.jiraUrl
              )
            : await apiService.sendJiraServerVerificationCode(this.verificationCode, this.token);
        localStorage.setItem("tokenId", response.tokenId);
        this.$store.commit("setTokenId", response.tokenId);
      } catch (e) {
        this.showToast(e);
      }
      this.$nextTick(() => {
        this.$bvModal.hide("modal-verification-code");
      });
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
