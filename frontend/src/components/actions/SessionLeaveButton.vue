<template>
  <div class="img-holderLeave mt-5">
    <div id="picture-holderLeave">
      <b-img :src="require('@/assets/LeaveButton.png')" id="pandaPictureLeave"/>
    </div>
    <b-button
      v-b-modal.close-session-modal
      variant="danger"
      class="mt-4 button"
      @click="leaveSession"
    >
      <b-icon-x />
      {{ $t("page.vote.button.leave.label") }}
    </b-button>
  </div>
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
