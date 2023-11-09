<template>
  <b-card class="landing-page-card text-center" :title="title">
    <b-card-text class="my-5">
      {{ description }}
    </b-card-text>
    <template #footer>
      <b-button class="landingPageCardButton" @click="onClick">
        {{ buttonText }}
      </b-button>
    </template>
  </b-card>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "LandingPageCard",
  props: {
    title: { type: String, required: true },
    description: { type: String, required: true },
    buttonText: { type: String, required: true },
    onClick: { type: Function, required: true },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.landing-page-card {
  border-radius: 2rem;
  background-color: var(--landingPageCardsBackground);
}
</style>
