<template>
  <div class="img-holderLeave" :class="isMobile ? '' : 'mt-5'">
    <div id="picture-holderLeave">
      <b-img id="pandaPictureLeave" :src="require('@/assets/LeaveButton.png')" />
    </div>
    <b-button
      v-b-modal.close-session-modal
      variant="danger"
      class="mt-4 button"
      @click="leaveSession"
    >
      <b-icon-x />
      {{ t("page.vote.button.leave.label") }}
    </b-button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Constants from "../../constants";
import { useDiveniStore } from "@/store";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SessionLeaveButton",
  props: {
    isMobile: { type: Boolean, default: false },
  },
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    return { store, t };
  },
  methods: {
    leaveSession() {
      const endPoint = `${Constants.webSocketUnregisterRoute}`;
      this.store.sendViaBackendWS(endPoint);
      this.store.clearStore();
      window.localStorage.removeItem("memberCookie");
      this.$router.push({ name: "LandingPage" });
    },
  },
});
</script>
