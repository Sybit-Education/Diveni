<template>
  <div
    :id="'memberCard-' + index"
    :style="`background-color: ${color};`"
    class="text-center"
  >
    <h1
      class="mt-3 fs-3-rem"
    >
      <strong
        :id="'estimate-' + index"
      >?</strong>
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
    index: { type: Number, required: true },
    name: { type: String, required: true },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.fs-3-rem{
  font-size: 5rem;
}
div{
  position: relative;
  bottom: -170px;
  width: 200px;
  height: 250px;
  /*justify-content: center;  !* Centering y-axis *!*/
  align-items: center; /* Centering x-axis */
  border-radius: 20%;
  border: 1px solid;
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
