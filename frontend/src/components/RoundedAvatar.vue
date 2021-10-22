<template>
  <div
    class="rounded-circle"
    :style="`background-color: ${color}`"
  >
    <img
      :src="require(`@/assets/${assetName}`)"
      width="44"
    >
  </div>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'RoundedAvatar',
  props: {
    color: { type: String, required: true },
    assetName: { type: String, required: true },
  },
  mounted() {
    console.log(this.assetName);
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
div{
  width: 80px;
  height: 80px;
  padding-left: 8px;
  padding-bottom: 8px;
  padding-right: 8px;
  padding-top: 8px;
  display: flex;
  justify-content: center;  /* Centering y-axis */
  align-items :center; /* Centering x-axis */
}
</style>
