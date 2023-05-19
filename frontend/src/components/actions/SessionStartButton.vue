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
import Member from "@/model/Member";
export default Vue.extend({
  name: "SessionStartButton",
  props: {
    members: {
      type: Array,
      required: false,
      default: () => [] as Array<Member>,
    },
    hostVoting: { type: Boolean, required: true }
  },
  methods: {
    sendStartEstimationMessages() {
      const endPoint = Constants.webSocketStartPlanningRoute;
      this.$store.commit("sendViaBackendWS", {
        endPoint, data: this.hostVoting
      });
      this.$emit("clicked");
    },
  },
});
</script>
