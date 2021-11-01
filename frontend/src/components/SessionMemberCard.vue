<template>
  <div
    :style="`background-color: ${color};`"
    class="text-center card mt-4 mx-3"
    :class="{ highlight: (highest || lowest) && estimateFinished }"
  >
    <h1
      class="mt-3 fs-3-rem"
    >
      <strong v-if="estimation === null || !estimateFinished">?</strong>
      <strong v-else>{{ estimation }}</strong>
    </h1>
    <img
      :src="require(`@/assets/${assetName}`)"
      :alt="altAttribute.toLowerCase() + ' image'"
      width="50"
      class="mt-4"
    >
    <h4
      class="mt-2"
    >
      <strong>{{ name }}</strong>
    </h4>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'SessionMemberCard',
  props: {
    assetName: { type: String, required: true },
    altAttribute: { type: String, required: false, default: 'animal' },
    color: { type: String, required: true },
    estimation: { type: Number, required: false, default: null },
    name: { type: String, required: true },
    estimateFinished: { type: Boolean, required: false, default: false },
    highest: { type: Boolean, required: false, default: false },
    lowest: { type: Boolean, required: false, default: false },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.fs-3-rem{
  font-size: 5rem;
}
.card{
  position: relative;
  width: 280px;
  height: 370px;
  justify-content: center;  /* Centering y-axis */
  align-items :center; /* Centering x-axis */
  border-radius: 5%;
  display: flex;
  flex-direction: column;
}
h4 {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width:100px;
}
.highlight {
  transform: scale(1.10);
  border-width: 3px;
  border-color: darkred;
}
</style>
