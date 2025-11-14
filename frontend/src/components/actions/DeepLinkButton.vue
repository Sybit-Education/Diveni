<template>
  <b-button class="copy-btn" variant="outline-dark" @click="copyDeepLink">
    <b-icon icon="clipboard" class="bIcons" />
    {{ t("session.prepare.step.wizard.deeplink.copyDeeplink") }}
  </b-button>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "DeepLinkButton",
  inheritAttrs: false,
  props: {
    mode: { type: String, required: true },
    cardSetType: { type: String, required: true },
    activeValues: { type: Array as () => string[], required: true },
    timer: { type: Number, required: true },
    hostVoting: { type: Boolean, required: true },
    password: { type: String, default: "" },
  },
  data() {
    const toast = useToast();
    const { t } = useI18n();
    return { toast, t };
  },
  methods: {
    copyDeepLink(): void {
      const passwordParam = this.password ? `&password=${encodeURIComponent(this.password)}` : "";
      const baseUrl = `${window.location.origin}/prepare`;

      const queryParams = new URLSearchParams({
        mode: this.mode,
        cardSetType: this.cardSetType,
        set: this.activeValues.join(","),
        timer: String(this.timer),
        hostVoting: String(this.hostVoting),
      });

      const deepLink = `${baseUrl}?${queryParams.toString()}${passwordParam}`;

      navigator.clipboard
        .writeText(deepLink)
        .then(() => {
          this.toast.success(this.t("session.prepare.step.wizard.deeplink.copy"));
          this.$emit("copied", deepLink);
        })
        .catch((err: unknown) => {
          console.error("Failed to copy deep link", err);
          this.toast.error(this.t("session.prepare.step.wizard.deeplink.copyFailed"));
        });
    },
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/style/_variables.scss";
.copy-btn {
  background-color: var(--textAreaColour) !important;
  display: inline-flex !important;
  align-items: center !important;
  margin: 0.5rem !important;
}

.copy-btn:hover {
  background-color: var(--textAreaColourHovered) !important;
  color: var(--text-primary-color) !important;
}
</style>
