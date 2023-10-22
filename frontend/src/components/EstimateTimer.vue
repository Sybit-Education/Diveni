<template>
  <div class="d-flex justify-content-end estimate-timer">
    <b-card-text :style="`color: ${textColor}`" class="timer">
      {{ formatTimer() }}
    </b-card-text>
  </div>

</template>

<script lang="ts">
import constants from "@/constants";
import Vue from "vue";

export default Vue.extend({
  name: "EstimateTimer",
  props: {
    startTimestamp: { type: String, required: true },
    duration: { type: Number, required: true },
    pauseTimer: { type: Boolean, required: true },
    member: { type: String, required: false, default: "" },
    votingStarted: { type: Boolean, required: true },
  },
  data() {
    return {
      timerCount: 0,
      intervalHandler: -1,
    };
  },
  computed: {
    textColor(): string {
      if (this.timerCount > (this.duration / 3) * 2) {
        return "#229954";
      }
      if (this.timerCount > this.duration / 3) {
        return "#D4AC0D";
      }
      if (this.timerCount === 0) {
        return "grey";
      }
      return "#CB4335";
    },
  },
  watch: {
    startTimestamp() {
      this.resetTimer();
      this.startInterval();
    },
    pauseTimer(newValue) {
      if (newValue) {
        clearInterval(this.intervalHandler);
      }
    },
  },
  created() {
    this.timerCount = this.duration;
    if (this.votingStarted) {
      if (this.startTimestamp) {
        this.startInterval();
      }
    }
  },
  beforeDestroy() {
    clearInterval(this.intervalHandler);
  },
  methods: {
    formatTimer() {
      if (this.startTimestamp === "" || this.duration === 0) {
        return "";
      }
      const minutes = Math.floor(this.timerCount / 60);
      const seconds = (this.timerCount % 60).toString().padStart(2, "0");
      return `${minutes}:${seconds}`;
    },
    resetTimer() {
      this.timerCount = this.duration;
    },
    startInterval() {
      clearInterval(this.intervalHandler);
      if (this.duration === 0) {
        return;
      }
      this.intervalHandler = setInterval(() => {
        if (this.timerCount > 0) {
          const startTime = new Date(this.startTimestamp).getTime();
          const currentTime = new Date().getTime();
          this.timerCount = Math.ceil(this.duration - (currentTime - startTime) / 1000);
          if (this.timerCount > this.duration || this.timerCount < 0) {
            clearInterval(this.intervalHandler);
            this.localClockTimer();
          }
        } else {
          this.$emit("timerFinished");
          clearInterval(this.intervalHandler);
        }
      }, 100);
    },
    async localClockTimer() {
      if (this.member !== '') {
        const response = (await this.axios.get(constants.backendURL + "/get-timer-value",{
              params: {
                memberID: this.member,
              },
        })).data;
        this.timerCount = Math.ceil(this.duration - (response / 1000));
        this.intervalHandler = setInterval(() => {
          if (this.timerCount > 0) {
            this.timerCount = this.timerCount - 1;
          } else {
            this.$emit("timerFinished");
            clearInterval(this.intervalHandler);
          }
        }, 1000);
      }
    }
  },
});
</script>

<style scoped>
.estimate-timer {
  font-size: 1.5rem;
  font-weight: 700;
  border-radius: 0.5rem;
}
.card-body {
  padding: 0.5rem;
}

.card-text {
  line-height: 485%;
  text-align: center;;
}

.timer {
  width: 100px;
  height: 117px;
  background-image: url('@/assets/Timer.png');
  background-size: contain;
  user-select: none;
}
</style>
