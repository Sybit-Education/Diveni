<template>
  <div></div>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import { useToast } from "vue-toastification";

export default defineComponent({
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
        useToast().warning(this.$t("session.notification.messages.memberLeft"));
      } else if (message.type == "MEMBER_JOINED") {
        useToast().info(this.$t("session.notification.messages.memberJoined"));
      }
    },
  },
});
</script>
