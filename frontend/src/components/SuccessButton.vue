<template>
  <b-button
    variant="success"
    :disabled="disabled"
    @click="onClick"
  >
    {{ buttonText }}
  </b-button>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'SuccessButton',
  props: {
    buttonText: { type: String, required: true },
    disabled: { type: Boolean, required: true },
    onClick: { type: Function, required: true },
  },
});
</script>
