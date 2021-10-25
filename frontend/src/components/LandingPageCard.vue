<template>
  <b-card
    id="landing-page-card"
    class="p-3 text-center"
  >
    <h3 class="text-center">
      {{ title }}
    </h3>
    <div
      class="mt-5"
    >
      {{ description }}
    </div>
    <b-button
      class="mt-5"
      variant="success"
      :disabled="false"
      @click="onClick"
    >
      {{ buttonText }}
    </b-button>
  </b-card>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'LandingPageCard',
  props: {
    title: { type: String, required: true },
    description: { type: String, required: true },
    buttonText: { type: String, required: true },
    onClick: { type: Function, required: true },
  },
});
</script>

<style scoped>
#landing-page-card {
  border-radius: 20px;
}
h3 {
  font-weight:500;
}
</style>
