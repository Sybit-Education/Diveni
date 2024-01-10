<template>
  <div></div>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "NotifyMemberComponent",
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    return { store, toast, t };
  },
  computed: {
    notifications() {
      return this.store.notifications;
    },
  },
  watch: {
    notifications() {
      const notification = this.store.notifications.at(-1);
      if (notification) {
        this.showToast(notification);
      }
    },
  },
  methods: {
    showToast(message) {
      if (!message) {
        return;
      }
      if (message.type === "ADMIN_LEFT") {
        const hostLeft = this.t("session.notification.messages.hostLeft");
        this.toast.warning(hostLeft);
        this.$emit("hostLeft");
      } else if (message.type === "ADMIN_JOINED") {
        this.toast.info(this.t("session.notification.messages.hostJoined"));
        this.$emit("hostJoined");
      }
    },
  },
});
</script>
