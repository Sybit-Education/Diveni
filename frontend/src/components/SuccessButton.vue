<template>
  <v-btn color="green white--text">
    {{ buttonText }}
  </v-btn>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'SuccessButton',
  props: {
    buttonText: { type: String, required: true },
  },
});
</script>
