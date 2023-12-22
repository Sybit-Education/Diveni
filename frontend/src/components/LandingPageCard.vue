<template>
  <b-card class="landing-page-card text-center" :title="title">
    <b-card-text class="my-5">
      {{ description }}
    </b-card-text>
    <template #footer>
      <b-button :variant="buttonVariant" class="landingPageCardButton" @click="onClick">
        {{ buttonText }}
      </b-button>
    </template>
  </b-card>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "LandingPageCard",
  props: {
    title: { type: String, required: true },
    description: { type: String, required: true },
    buttonText: { type: String, required: true },
    onClick: { type: Function, required: true },
    buttonVariant: { type: String, required: false, default: "primary"},
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.landing-page-card {
  border-radius: 2rem;
  background-color: var(--blurBackGroundColor);
}
</style>
