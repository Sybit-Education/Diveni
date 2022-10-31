<template>
  <b-card id="estimate-timer" class="d-flex justify-content-end estimate-timer">
    <b-card-text :style="`color: ${textColor}`">
      {{ formatTimer() }}
    </b-card-text>
  </b-card>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "EstimateTimer",
  props: {
    startTimestamp: { type: String, required: true },
    duration: { type: Number, required: true },
    pauseTimer: { type: Boolean, required: true },
  },
  emits: [ 'timerFinished' ],
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
    if (this.startTimestamp) {
      this.startInterval();
    }
  },
  beforeUnmount() {
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
      this.intervalHandler = window.setInterval(() => {
        if (this.timerCount > 0) {
          const startTime = new Date(this.startTimestamp).getTime();
          const currentTime = new Date().getTime();
          this.timerCount = Math.ceil(
            this.duration - (currentTime - startTime) / 1000
          );
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
.estimate-timer {
  font-size: 1.5rem;
  font-weight: 700;
  border-radius: 0.5rem;
  min-width: 8rem;
  text-align: right;
}
.card-body {
  padding: 0.5rem;
}
</style>
