<template>
  <div>
    <Vue2InteractDraggable
      v-if="!dragged"
      :interact-max-rotation="0"
      :interact-out-of-sight-x-coordinate="500"
      :interact-x-threshold="200"
      :interact-lock-x-axis="true"
      :interact-lock-swipe-down="true"
      @draggedUp="draggedUp"
    >
      <div
        id="swipe-card"
        class="flicking-panel"
        :style="swipeableCardBackgroundColor"
      >
        <div id="text">
          {{ number }}
        </div>
      </div>
    </Vue2InteractDraggable>
    <div
      v-else
      id="swiped-card"
      class="flicking-panel"
    >
      <div id="text">
        💪
      </div>
    </div>
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
  },
  data() {
    return {
      dragged: false,
    };
  },
  computed: {
    swipeableCardBackgroundColor():string {
      const { r, g, b } = Constants.default.hexToRgb(this.hexColor);
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
      this.dragged = !this.dragged;
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
#swiped-card{
  width: 280px;
  height: 370px;
  justify-content: center;  /* Centering y-axis */
  align-items :center; /* Centering x-axis */
  border-radius: 5%;
  display: flex;
  background-color: rgb(230, 225, 228);
  flex-direction: column;
}
#text {
  font-size: 200px;
  font-weight: 500;
}
</style>
