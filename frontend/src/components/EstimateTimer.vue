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
    isEnable: { type: Boolean, required: true },
  },
  data() {
    return {
      timerCount: this.timer,
      enable: this.isEnable,
    };
  },
  watch: {
    enable(timerEnable) {
      if (timerEnable) {
        setTimeout(() => {
          if (this.timerCount > 1) {
            this.timerCount -= 1;
          }
        }, 1000);
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
