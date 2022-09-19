<template>
  <div></div>
</template>
<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "NotifyHostComponent",

  computed: {
    notifications() {
      return this.$store.state.notifications;
    },
  },
  watch: {
    notifications(notifications) {
      this.showToast(notifications.at(-1));
    },
  },
  methods: {
    showToast(message) {
      if (message.type == "MEMBER_LEFT") {
        this.$toast.warning(this.$t("session.notification.messages.memberLeft"));
      } else if (message.type == "MEMBER_JOINED") {
        this.$toast.info(this.$t("session.notification.messages.memberJoined"));
      }
    },
  },
});
</script>
