<template>
  <div>
    <Vue2InteractDraggable
      :key="dragged"
      :interact-max-rotation="0"
      :interact-out-of-sight-x-coordinate="0"
      :interact-out-of-sight-y-coordinate="0"
      :interact-x-threshold="200"
      :interact-lock-x-axis="true"
      :interact-lock-y-axis="dragged"
      :interact-lock-swipe-down="true"
      @draggedUp="draggedUp"
    >
      <div
        id="swipe-card"
        class="flicking-panel"
        :style="swipeableCardBackgroundColor"
      >
        <div id="text">
          {{ dragged ? '💪' : number }}
        </div>
      </div>
    </Vue2InteractDraggable>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import { Vue2InteractDraggable } from 'vue2-interact';
import confetti from 'canvas-confetti';
import * as Constants from '../constants';

export default Vue.extend({
  name: 'MemberEstimateCard',
  components: {
    Vue2InteractDraggable,
  },
  props: {
    number: { type: Number, required: true },
    hexColor: { type: String, required: true },
    dragged: { type: Boolean, required: true },
  },
  computed: {
    swipeableCardBackgroundColor():string {
      let { r, g, b } = Constants.default.hexToRgb(this.hexColor);
      r = !this.dragged ? r : 230;
      g = !this.dragged ? g : 225;
      b = !this.dragged ? b : 228;
      return `background-color: rgb(${r - this.number * 2}, ${g}, ${b});`;
    },
  },
  methods: {
    draggedUp() {
      confetti({
        particleCount: 300,
        startVelocity: 30,
        spread: 360,
      });
      this.$emit('sentEstimation', {
        estimation: this.number,
      });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#swipe-card{
  width: 280px;
  height: 370px;
  justify-content: center;  /* Centering y-axis */
  align-items :center; /* Centering x-axis */
  border-radius: 5%;
  display: flex;
  flex-direction: column;
}
#text {
  font-size: 200px;
  font-weight: 500;
}
</style>
