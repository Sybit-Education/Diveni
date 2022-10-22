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
import { defineComponent } from "vue";

export default defineComponent({
  name: "SessionLeaveButton",
  methods: {
    leaveSession() {
      window.localStorage.removeItem("memberCookie");
      this.$router.push({ name: "LandingPage" });
    },
  },
});
</script>
