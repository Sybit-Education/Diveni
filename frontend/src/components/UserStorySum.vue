<template>
  <div>
    <b-row v-if="userStorySum > 0" align-h="between">
      <b-col>
        <h4 class="float-start">{{ $t("page.session.before.userStories.sum") }}</h4>
        <h4 class="float-end me-4">{{ userStorySum }}</h4>
      </b-col>
    </b-row>
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

<style scoped></style>
