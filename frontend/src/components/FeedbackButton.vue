<template>
  <div class="feedback">
    <a
      id="feedback-btn"
      target="_blank"
      href="https://github.com/Sybit-Education/Diveni/issues/new/choose"
      >{{ $t("general.feedback") }}</a
    >
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "FeedbackButton",
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.feedback {
  display: inline-block;
  left: 100%;
  position: sticky;
  top: 50%;
  z-index: 100;
  transform-origin: bottom right;
  transform: rotate(270deg);

  #feedback-btn {
    padding: 5px 15px;
    background-color: var(--text-primary-color);
    color: var(--accent-color);
    font-size: 17px;
    border-radius: 0;
  }
  #feedback-btn:hover {
    text-decoration: none;
  }
}
</style>
