<template>
  <b-container class="bv-example-row bv-example-row-flex-cols">
    <b-row align-v="stretch text-center">
      <desktop-card
      v-for="item in cardSet" :key="item"
      :cardName="item"/>
    </b-row>
  </b-container>
</template>

<script lang="ts">
import Vue from 'vue';
import DesktopCard from './DesktopCard.vue';

export default Vue.extend({
  components: { DesktopCard },
  name: 'DesktopGrid',
  props: {
    cardSet: { type: Array, required: true },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#text {
  font-size: 20px;
  font-weight: 500;
}
</style>
