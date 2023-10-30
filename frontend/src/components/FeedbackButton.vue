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

<style scoped>
.feedback {
  display: flex;
  justify-content: end;
  position: sticky;
  top: 50%;
  z-index: 100;
}
#feedback-btn {
  padding: 5px 15px;
  background-color: var(--feedbackButtonBackground);
  color: white;
  font-size: 16px;
  border-radius: 0;
  transform: rotate(270deg) translate(50%, -50%);
  transform-origin: right;
}
#feedback-btn:hover {
  text-decoration: none;
}
</style>
