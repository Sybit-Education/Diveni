<template>
  <b-card class="landing-page-card text-center" :title="title">
    <b-card-text class="my-5">
      {{ description }}
    </b-card-text>
    <b-button variant="success" @click="onClick">
      {{ buttonText }}
    </b-button>
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
  },
});
</script>

<style scoped>
.landing-page-card {
  border-radius: 0.5rem;
}
</style>
