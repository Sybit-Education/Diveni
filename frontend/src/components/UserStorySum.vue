<template>
  <div v-if="userStorySum > 0" align-h="between">
    <h4>
      {{ $t("page.session.before.userStories.sum") }}
      <span>{{ userStorySum }}</span>
    </h4>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
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

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped></style>
