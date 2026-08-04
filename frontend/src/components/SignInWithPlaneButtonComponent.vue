<template>
  <div>
    <b-button variant="primary" :disabled="disabled || connecting" @click="connectToPlane">
      {{ connecting ? "Connecting to Plane..." : "Connect to Plane" }}
    </b-button>
  </div>
</template>

<script lang="ts">
import apiService from "@/services/api.service";
import { defineComponent } from "vue";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SignInWithPlaneButtonComponent",
  props: {
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    return { store, toast, t };
  },
  data() {
    return {
      connecting: false,
    };
  },
  methods: {
    async connectToPlane() {
      this.connecting = true;
      try {
        const response = await apiService.connectToPlane();
        localStorage.setItem("tokenId", response.tokenId);
        this.store.setTokenId(response.tokenId);
      } catch (error) {
        this.toast.error(this.t("session.notification.messages.issueTrackerLoginFailed"));
        console.error(error);
      } finally {
        this.connecting = false;
      }
    },
  },
});
</script>
