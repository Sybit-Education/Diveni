<template>
  <div></div>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "NotifyHostComponent",
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
    notifications(notifications) {
      const latestNotification = notifications.at(-1);
      if (latestNotification) {
        this.showToast(latestNotification);
      }
    },
  },
  methods: {
    showToast(message) {
      if (message.type == "MEMBER_LEFT") {
        this.toast.warning(this.t("session.notification.messages.memberLeft"));
      } else if (message.type == "MEMBER_JOINED") {
        this.toast.info(this.t("session.notification.messages.memberJoined"));
      }
    },
  },
});
</script>
