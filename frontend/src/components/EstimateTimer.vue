<template>
  <div id="estimate-timer" class="d-flex justify-content-end">
    <h2>
      {{ formatTimer() }}
    </h2>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'EstimateTimer',
  props: {
    timer: { type: Number, required: true },
    timerTriggered: { type: Number, required: true },
    startTimerOnComponentCreation: { type: Boolean, required: false },
    initialTimer: { type: Number, required: true },
  },
  data() {
    return {
      timerCount: 60,
      intervalHandler: -1,
    };
  },
  watch: {
    timerTriggered() {
      this.countdown();
    },
  },
  created() {
    if (this.startTimerOnComponentCreation) {
      this.countdown();
    }
  },
  beforeDestroy() {
    clearInterval(this.intervalHandler);
  },
  methods: {
    formatTimer() {
      const minutes = Math.floor(this.timerCount / 60);
      const seconds = (this.timerCount % 60).toString().padStart(2, '0');
      return `${minutes}:${seconds}`;
    },
    resetTimer() {
      this.timerCount = this.initialTimer;
    },
    countdown() {
      clearInterval(this.intervalHandler);
      this.resetTimer();
      this.intervalHandler = setInterval(() => {
        if (this.timerCount > 0) {
          this.timerCount -= 1;
        } else {
          clearInterval(this.intervalHandler);
        }
      }, 1000);
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
