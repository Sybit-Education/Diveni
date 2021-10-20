<template>
  <v-card
    elevation="8"
    rounded="xl"
    class="pa-8 ma-4"
    color="lightgray"
  >
    <div class="text-center text-h4">
      {{ title }}
    </div>
    <div class="text-center text-b1">
      {{ description }}
    </div>
    <v-card-actions class="justify-center">
      <success-button :button-text="buttonText" />
    </v-card-actions>
  </v-card>
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
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
