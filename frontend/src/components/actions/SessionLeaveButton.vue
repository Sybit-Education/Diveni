<template>
  <b-button
    v-b-modal.close-session-modal
    style="max-height: 40px"
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

export default Vue.extend({
  name: "SessionLeaveButton",
  methods: {
    leaveSession() {
      window.localStorage.removeItem("memberCookie");
      this.$router.push({ name: "LandingPage" });
    },
  },
});
</script>
