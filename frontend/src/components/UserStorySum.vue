<template>
  <div v-if="userStorySum > 0" align-h="between">
    <h4>
      {{ t("page.session.before.userStories.sum") }}
      <span>{{ userStorySum }}</span>
    </h4>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useDiveniStore } from "@/store";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "UserStorySumComponent",
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    return { store, t };
  },
  computed: {
    userStorySum() {
      let sum = 0;
      this.store.userStories.forEach((userStory) => {
        if (Number.isInteger(parseInt(userStory.estimation ?? ""))) {
          sum += parseInt(userStory.estimation ?? "");
        }
      });
      return sum;
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped></style>
