<template>
  <b-card class="p-3 text-center">
    <h3 class="text-center">
      {{ title }}
    </h3>
    <div
      class="mt-5"
    >
      {{ description }}
    </div>
    <success-button
      class="mt-5"
      :button-text="buttonText"
      :on-click="onClick"
    />
  </b-card>
</template>

<script lang="ts">
import Vue from 'vue';
import SuccessButton from './SuccessButton.vue';

export default Vue.extend({
  name: 'LandingPageCard',
  components: { SuccessButton },
  props: {
    title: { type: String, required: true },
    description: { type: String, required: true },
    buttonText: { type: String, required: true },
    onClick: { type: Function, required: true },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
