<template>
  <b-modal
    v-model="checkIfNotInstalled"
    class="test"
    centered
    title="Download Diveni"
    @ok="install()"
    @hide="closeModal()"
  >
    {{ t("page.landing.install") }}
  </b-modal>
</template>

<script lang="ts">
import { defineComponent, ref, Ref } from "vue";
import { useI18n } from "vue-i18n";

interface BeforeInstallPromptEvent extends Event {
  prompt(): void;
  userChoice: Promise<{
    outcome: "accepted" | "dismissed";
  }>;
}

export default defineComponent({
  name: "DownloadPWAModal",
  setup() {
    const deferredPrompt: Ref<BeforeInstallPromptEvent | null> = ref(null);
    const checkIfNotInstalled = false;
    window.addEventListener("beforeinstallprompt", (e) => {
      e.preventDefault();
      deferredPrompt.value = e as BeforeInstallPromptEvent;
    });

    window.addEventListener("appinstalled", () => {
      deferredPrompt.value = null;
    });
    const { t } = useI18n();
    return { deferredPrompt, checkIfNotInstalled, t };
  },
  computed: {
    isMobile() {
      return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
        navigator.userAgent
      );
    },
  },
  created() {
    const isInStandaloneMode = () =>
      window.matchMedia("(display-mode: standalone)").matches ||
      document.referrer.includes("android-app://");
    this.checkIfNotInstalled =
      this.isMobile && !isInStandaloneMode() && !localStorage.getItem("downloadPWA");
  },
  methods: {
    closeModal() {
      localStorage.setItem("downloadPWA", "true");
    },
    async install() {
      console.log("Installing...");
      if (this.deferredPrompt) {
        this.deferredPrompt.prompt();
        this.deferredPrompt.userChoice.then((choiceResult) => {
          if (choiceResult.outcome === "accepted") {
            console.log("User accepted the Diveni prompt");
          } else {
            console.log("User dismissed the Diveni prompt");
          }
          this.deferredPrompt = null;
        });
      }
    },
  },
});
</script>

<style scoped lang="scss"></style>
