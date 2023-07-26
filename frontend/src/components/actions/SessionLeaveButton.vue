<template>
  <b-button
    v-b-modal.close-session-modal
    style="max-height: 40px; border-radius: 2rem;"
    variant="danger"
    class="mt-4"
    @click="leaveSession"
  >
    <b-icon-x />
    {{ $t("page.vote.button.leave.label") }}
  </b-button>
</template>

<script lang="ts">
import Vue from "vue";
import Constants from "../../constants";

export default Vue.extend({
  name: "SessionLeaveButton",
  methods: {
    leaveSession() {
      const endPoint = `${Constants.webSocketUnregisterRoute}`;
      this.$store.commit("sendViaBackendWS", { endPoint, data: null });
      this.$store.commit("clearStore");
      window.localStorage.removeItem("memberCookie");
      this.$router.push({ name: "LandingPage" });
    },
  },
});
</script>
