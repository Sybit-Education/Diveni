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
import Vue from "vue";
import Constants from "@/constants";

export default Vue.extend({
  name: "SessionStartButton",
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
