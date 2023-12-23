<template>
  <div ref="card">
    <div v-if="isMobile" :key="dragged">
      <div
        ref="swippable"
        class="flicking-panel swipe-card"
        :style="[swipeableCardBackgroundColor, { marginBottom: position }]"
      >
        <div class="text" @click="onCardClicked()">
          {{ dragged ? "💪" : voteOption }}
        </div>
      </div>
    </div>
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
// eslint-disable-next-line
import { defineComponent, ref } from "vue";
import confetti from "canvas-confetti";
import Constants from "../constants";
import Hammer from "hammerjs";

export default defineComponent({
  name: "MemberVoteCard",
  props: {
    voteOption: { type: String, required: true },
    index: { type: Number, required: true },
    hexColor: { type: String, required: true },
    dragged: { type: Boolean, required: true },
    isMobile: { type: Boolean, required: true },
    disabled: { type: Boolean, default: false },
  },
  emits: ["sent-vote"],
  data() {
    return {
      position: 0,
    };
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
  mounted() {
    const maxUpPos = 200;
    const threshold = maxUpPos / 2;

    if (typeof Hammer !== "undefined" && this.isMobile) {
      const ref = this.$refs.swippable as HTMLElement;
      const hammer = new Hammer(ref);

      hammer.get("swipe").set({ direction: Hammer.DIRECTION_UP });
      hammer.get("pan").set({ direction: Hammer.DIRECTION_VERTICAL });
      hammer.on("swipe", this.draggedUp);
      hammer.on("pan", (ev) => {
        if (ev.deltaY <= 0 && ev.deltaY >= -maxUpPos) {
          this.position = ev.deltaY;
          ref.style.transform = `translateY(${this.position}px)`;
        }
      });
      hammer.on("panend", () => {
        ref.style.transform = `translateY(0)`;
        if (this.position < -threshold) {
          this.draggedUp();
        }
      });
    }
  },
  methods: {
    onCardClicked() {
      this.draggedUp();
    },
    draggedUp() {
      if (!this.disabled) {
        confetti({
          particleCount: 50,
          startVelocity: 50,
          spread: 100,
        });
        this.$emit("sent-vote", {
          vote: this.voteOption,
        });
      }
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.swipe-card {
  width: 148px;
  height: 202px;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
  border-radius: 5%;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  margin-bottom: 5%;
}

.text {
  font-size: 5rem;
  font-weight: 500;
  color: black;
}
</style>
