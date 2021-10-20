<template>
  <v-card elevation="8" class="pa-8">
    <div class="text-center text-h4">{{ title }}</div>
    <div class="text-center text-b1 px-16 py-8">{{ description }}</div>
    <v-card-actions class="justify-center">
      <v-btn color="primary">
        {{ buttonText }}
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "LandingPageCard",
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
