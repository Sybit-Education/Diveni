<template>
  <div v-if="isPlanningStart">
    <b-button v-b-modal.close-session-modal variant="danger">
      <b-icon-x />
      {{ $t("page.session.during.estimation.buttons.finish") }}
    </b-button>
    <b-modal
      id="close-session-modal"
      :title="$t('page.session.close.popup')"
      :cancel-title="$t('page.session.close.button.cancel')"
      :ok-title="$t('page.session.close.button.ok')"
      @ok="closeSession"
    >
      <p class="my-4">
        {{ $t("page.session.close.description1") }}
      </p>
      <p v-if="userStoryMode !== 'NO_US'">
        {{ $t("page.session.close.description2") }}
      </p>
    </b-modal>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Constants from "@/constants";

export default defineComponent({
  name: "SessionCloseButton",
  props: {
    isPlanningStart: {
      type: Boolean,
      required: true,
    },
    userStoryMode: {
      type: String,
      required: true,
    },
  },
  methods: {
    closeSession() {
      this.sendCloseSessionCommand();
      window.localStorage.removeItem("adminCookie");
      if (this.userStoryMode !== "NO_US") {
        this.$router.push({ name: "ResultPage" });
      } else {
        this.$store.commit("clearStore");
        this.$router.push({ name: "LandingPage" });
      }
    },
    sendCloseSessionCommand() {
      const endPoint = `${Constants.webSocketCloseSessionRoute}`;
      this.$store.commit("sendViaBackendWS", { endPoint });
    },
  },
});
</script>
