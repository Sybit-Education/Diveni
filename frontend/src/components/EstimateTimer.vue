<template>
  <div id="estimate-timer" class="d-flex justify-content-end">
    <h2 :style="`color: ${textColor}`">
      {{ formatTimer() }}
    </h2>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "EstimateTimer",
  props: {
    startTimestamp: { type: String, required: true },
    duration: { type: Number, required: true },
    pauseTimer: { type: Boolean, required: true },
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
        return "black";
      }
      return "#CB4335";
    },
  },
  watch: {
    startTimestamp(newVal) {
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
    if (this.startTimestamp) {
      this.startInterval();
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
          let startTime = new Date(this.startTimestamp).getTime();
          let currentTime = new Date().getTime();
          this.timerCount = Math.ceil(this.duration - (currentTime - startTime) / 1000);
        } else {
          this.$emit("timerFinished");
          clearInterval(this.intervalHandler);
        }
      }, 100);
    },
  },
});
</script>

<style scoped>
#estimate-timer {
  border-radius: 20px;
}

h3 {
  font-weight: 500;
}
</style>
