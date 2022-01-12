<template>
  <div>
    <Vue2InteractDraggable
      v-if="isMobile"
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
      <div class="flicking-panel swipe-card" :style="swipeableCardBackgroundColor">
        <div class="text">
          {{ dragged ? "💪" : voteOption }}
        </div>
      </div>
    </Vue2InteractDraggable>
    <div v-else>
      <div
        class="flicking-panel swipe-card"
        :style="swipeableCardBackgroundColor"
        @click="onCardClicked()"
      >
        <div class="text">
          {{ dragged ? "💪" : voteOption }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Vue2InteractDraggable } from "vue2-interact";
import confetti from "canvas-confetti";
import Constants from "../constants";

export default Vue.extend({
  name: "MemberVoteCard",
  components: {
    Vue2InteractDraggable,
  },
  props: {
    voteOption: { type: String, required: true },
    index: { type: Number, required: true },
    hexColor: { type: String, required: true },
    dragged: { type: Boolean, required: true },
    isMobile: { type: Boolean, required: true },
  },
  computed: {
    swipeableCardBackgroundColor(): string {
      let { r, g, b } = Constants.hexToRgb(this.hexColor);
      r = !this.dragged ? r : 230;
      g = !this.dragged ? g : 225;
      b = !this.dragged ? b : 228;
      return `background-color: rgb(${r - this.index ** 2}, ${g}, ${b});`;
    },
  },
  methods: {
    onCardClicked() {
      this.draggedUp();
    },
    draggedUp() {
      confetti({
        particleCount: 50,
        startVelocity: 50,
        spread: 100,
      });
      this.$emit("sentVote", {
        vote: this.voteOption,
      });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.swipe-card {
  width: 280px;
  height: 370px;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
  border-radius: 5%;
  display: flex;
  flex-direction: column;
}
.text {
  font-size: 7rem;
  font-weight: 500;
}
</style>
