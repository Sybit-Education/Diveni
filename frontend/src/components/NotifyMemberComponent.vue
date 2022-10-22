<template>
  <div></div>
</template>
<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "NotifyMemberComponent",
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
      if (message.type === "ADMIN_LEFT") {
        const hostLeft = this.$t("session.notification.messages.hostLeft");
        this.$toast.warning(hostLeft);
        this.$emit("hostLeft");
      } else if (message.type === "ADMIN_JOINED") {
        this.$toast.info(this.$t("session.notification.messages.hostJoined"));
        this.$emit("hostJoined");
      }
    },
  },
});
</script>
