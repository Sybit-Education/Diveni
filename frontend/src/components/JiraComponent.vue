<template>
  <div>
    <b-container>
      <h5>{{ $t("session.prepare.step.selection.mode.description.withJira.subtitle") }}</h5>
      <ul>
        <li>
          {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine1") }}
        </li>
        <li>
          {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine2") }}
        </li>
        <li>
          {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine3") }}
        </li>
        <li>
          {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine4") }}
        </li>
      </ul>
      <b-row class="mt-5">
        <b-col>
          <b-button variant="success" @click="signInWithJira()">
            {{
              $t(
                "session.prepare.step.selection.mode.description.withJira.buttons.signInWithJira.label"
              )
            }}
          </b-button>
        </b-col>
      </b-row>
      <b-row class="mt-3">
        <b-col>
          <b-form>
            <b-form-group label-for="input-verification-code">
              <b-form-input
                id="input-verification-code"
                v-model="verificationCode"
                :placeholder="
                  $t(
                    'session.prepare.step.selection.mode.description.withJira.inputs.verificationCode.placeholder'
                  )
                "
              />
            </b-form-group>
          </b-form>
        </b-col>
      </b-row>
      <b-row class="mt-3">
        <b-col>
          <b-button variant="success" @click="signInWithJira()">
            {{
              $t("session.prepare.step.selection.mode.description.withJira.buttons.submit.label")
            }}
          </b-button>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import apiService from "@/services/api.service";

export default Vue.extend({
  name: "JiraComponent",
  data() {
    return {
      verificationCode: "",
    };
  },
  methods: {
    async signInWithJira() {
      console.log("clicked");
      const tokenDto = await apiService.getJiraRequestToken();
      window.open(tokenDto.url, "_blank")?.focus();
    },
  },
});
</script>
