<template>
  <b-button
    variant="primary"
    size="lg"
    class="my-5"
    :disabled="!members || members.length < 1"
    @click="sendStartEstimationMessages"
  >
    {{ t("page.session.before.button") }}
  </b-button>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Constants from "@/constants";
import Member from "@/model/Member";
import { useDiveniStore } from "@/store";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "SessionStartButton",
  props: {
    hostVoting: { type: Boolean, required: true },
  },
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    return { store, t };
  },
  computed: {
    members() {
      return this.store.members;
    },
  },
  methods: {
    sendStartEstimationMessages() {
      const endPoint = Constants.webSocketStartPlanningRoute;
      this.store.sendViaBackendWS(endPoint, this.hostVoting);
      this.$emit("clicked");
    },
  },
});
</script>
