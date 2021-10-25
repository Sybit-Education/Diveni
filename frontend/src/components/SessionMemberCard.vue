<template>
  <div
    :style="`background-color: ${color};`"
    class="text-center"
  >
    <img
      :src="require(`@/assets/${assetName}`)"
      width="80"
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
    name: { type: String, required: true },
    color: { type: String, required: true },
    assetName: { type: String, required: true },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
div{
  width: 200px;
  height: 200px;
  justify-content: center;  /* Centering y-axis */
  align-items :center; /* Centering x-axis */
  border-radius: 100%;
  display: flex;
  flex-direction: column;
}
h4 {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width:100px;
}
</style>
