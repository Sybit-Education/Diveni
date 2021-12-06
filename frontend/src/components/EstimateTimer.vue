<template>
  <div id="estimate-timer" class="d-flex justify-content-end">
    <h2 :style="`color: ${textColor}`">
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
      if (this.timerCount > ((this.initialTimer / 3) * 2)) {
        return '#229954';
      }
      if (this.timerCount > ((this.initialTimer / 3))) {
        return '#D4AC0D';
      }
      if (this.timerCount === 0) {
        return 'black';
      }
      return '#CB4335';
    },
  },
  watch: {
    timerTriggered() {
      this.countdown();
    },
    pauseTimer(newValue) {
      if (newValue) {
        clearInterval(this.intervalHandler);
      }
    },
  },
  created() {
    this.timerCount = this.initialTimer;
    if (this.startTimerOnComponentCreation) {
      this.countdown();
    }
  },
  beforeDestroy() {
    clearInterval(this.intervalHandler);
  },
  methods: {
    formatTimer() {
      if (this.initialTimer === 0) {
        return '';
      }
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
          if (this.initialTimer !== 0) {
            this.$emit('timerFinished');
          }
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
