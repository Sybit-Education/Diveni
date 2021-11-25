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
    timer: { type: Number, required: true },
    isEnable: { type: Boolean, required: false },
  },
  data() {
    return {
      timerEnable: this.isEnable,
      timerCount: this.timer,
    };
  },
  watch: {
    timerCount: {
      handler(value) {
        if (value > 0 && this.timerEnable) {
          setTimeout(() => {
            this.timerCount -= 1;
          }, 1000);
        } else { this.timerCount = 60; }
      },
      immediate: true, // This ensures the watcher is triggered upon creation
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
