<template>
  <div
    :style="`background-color: ${color};`"
    class="text-center card mt-4 mx-3"
    :class="{ greyOut: (!highest && !lowest) && estimateFinished }"
  >
    <h1 class="mt-3 fs-3-rem">
      <strong v-if="!estimateFinished">?</strong>
      <strong v-if="estimation === null && estimateFinished">-</strong>
      <strong v-if="estimation !== null && estimateFinished">{{ estimation }}</strong>
    </h1>
    <img
      :src="require(`@/assets/${assetName}`)"
      width="50"
      class="mt-4"
      alt="image"
    >
    <h4 class="mt-2">
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
    color: { type: String, required: true },
    estimation: { type: String, required: false, default: null },
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
  width: 224px;
  height: 296px;
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
.greyOut {
  opacity: 0.5;
  transform: scale(0.8);
  /*border-width: 5px;*/
  /*border-color: darkred;*/
}
</style>
