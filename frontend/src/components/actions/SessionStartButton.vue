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
    team: { type: String, required: false, default: "" },
    members: {
      type: Array,
      required: false,
      default: () => [] as Array<Member>,
    },
  },
  data() {
    return {
      memberArray: [] as Array<Member>,
    };
  },
  methods: {
    sendStartEstimationMessages() {
      console.log("this.team: " + this.team);
      console.log("this.members: " + this.members.length);
      if (this.team === "") {
        const endPoint = Constants.webSocketStartPlanningRoute;
        this.$store.commit("sendViaBackendWS", { endPoint });
        this.$emit("clicked");
      } else {
        if (this.team === "Frontend") {
          this.members.forEach((member) => {
            if (member.jobTitle === "Frontend" || member.jobTitle === "FullStack") {
              this.memberArray.push(member);
            }
          });
        } else {
          this.members.forEach((member) => {
            if (member.jobTitle === "Backend" || member.jobTitle === "FullStack") {
              this.memberArray.push(member);
            }
          });
        }
        console.log("this MemberArray: " + this.memberArray.length);
        const endPoint = Constants.webSocketStartPlanningWithTeamsRoute;
        this.$store.commit("sendViaBackendWS", {
          endPoint,
          data: JSON.stringify(this.memberArray),
        });
        this.$emit("clicked");
      }
    },
  },
});
</script>
