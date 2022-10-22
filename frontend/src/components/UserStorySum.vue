<template>
  <div v-if="userStorySum > 0" align-h="between">
    <h4>
      {{ $t("page.session.before.userStories.sum") }}
      <span>{{ userStorySum }}</span>
    </h4>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "UserStorySumComponent",
  computed: {
    userStorySum() {
      let sum = 0;
      this.$store.state.userStories.forEach((userStory) => {
        if (Number.isInteger(parseInt(userStory.estimation))) {
          sum += parseInt(userStory.estimation);
        }
      });
      return sum;
    },
  },
});
</script>

<style scoped></style>
