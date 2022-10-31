<template>
  <b-button
    variant="success"
    :disabled="!members || members.length < 1"
    @click="sendStartEstimationMessages"
  >
    {{ $t("page.session.before.button") }}
  </b-button>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Constants from "@/constants";

export default defineComponent({
  name: "SessionStartButton",
  emits: [ 'clicked' ],
  computed: {
    members() {
      return this.$store.state.members;
    },
  },
  methods: {
    sendStartEstimationMessages() {
      const endPoint = Constants.webSocketStartPlanningRoute;
      this.$store.commit("sendViaBackendWS", { endPoint });
      this.$emit("clicked");
    },
  },
});
</script>
