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
    isEnabled: { type: Boolean, required: true },
  },
  data() {
    return {
      timerCount: this.timer,
      intervalHandler: -1,
    };
  },
  watch: {
    isEnabled(timerEnable) {
      console.log('enable changed');
      if (timerEnable) {
        this.intervalHandler = setInterval(() => {
          if (this.timerCount > 1) {
            this.timerCount -= 1;
          } else {
            clearInterval(this.intervalHandler);
          }
        }, 1000);
      } else {
        clearInterval(this.intervalHandler);
      }
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
