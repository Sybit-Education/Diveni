<template>
  <div class="img-holderLeave" :class="isMobile ? '' : 'mt-5'">
    <div id="picture-holderLeave">
      <b-img id="pandaPictureLeave" :src="leaveButtonImg" />
    </div>
    <b-button
      v-b-modal.close-session-modal
      variant="danger"
      class="mt-4 button"
      @click="leaveSession"
    >
      <i class="bi bi-x"></i>
      {{ t("page.vote.button.leave.label") }}
    </b-button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Constants from "../../constants";
import { useDiveniStore } from "@/store";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import leaveButtonImg from "@/assets/LeaveButton.png";

export default defineComponent({
  name: "SessionLeaveButton",
  props: {
    isMobile: { type: Boolean, default: false },
  },
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    const router = useRouter();
    return { store, t, router, leaveButtonImg };
  },
  methods: {
    leaveSession() {
      const endPoint = `${Constants.webSocketUnregisterRoute}`;
      this.store.sendViaBackendWS(endPoint);
      this.store.clearStore();
      window.localStorage.removeItem("memberCookie");
      this.router.push({ name: "LandingPage" });
    },
  },
});
</script>
