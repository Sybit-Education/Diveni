<template>
  <div id="estimate-timer" class="p-3 text-center">
    <h3 class="text-center">
      {{ timerCount }}
    </h3>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'EstimateTimer',
  props: {
    timer: { type: Number, required: false },
    timerTriggered: { type: Number, required: true },
    fromBeginning: { type: Boolean, required: false },
  },
  data() {
    return {
      timerCount: 60,
      intervalHandler: -1,
    };
  },
  created() {
    if (this.fromBeginning) {
      this.countdown();
    }
  },
  watch: {
    timerTriggered() {
      this.countdown();
    },
  },
  methods: {
    resetTimer(time) {
      this.timerCount = time;
    },
    countdown() {
      console.log('timertriggered method called');
      clearInterval(this.intervalHandler);
      this.resetTimer(60);
      this.intervalHandler = setInterval(() => {
        if (this.timerCount > 0) {
          this.timerCount -= 1;
        } else {
          clearInterval(this.intervalHandler);
        }
      }, 1000);
    },
  },
  beforeDestroy() {
    clearInterval(this.intervalHandler);
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
