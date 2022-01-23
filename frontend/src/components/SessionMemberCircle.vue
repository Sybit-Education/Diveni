<template>
  <div :style="`background-color: ${color};`" class="text-center">
    <img
      :src="require(`@/assets/${assetName}`)"
      :alt="altAttribute.toLowerCase() + ' image'"
      width="70"
    />
    <h4 class="m-2">
      <strong>{{ name }}</strong>
    </h4>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "SessionMemberCircle",
  props: {
    assetName: { type: String, required: true },
    altAttribute: { type: String, required: false, default: "animal" },
    name: { type: String, required: true },
    color: { type: String, required: true },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
div {
  width: 150px;
  height: 150px;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
  border-radius: 100%;
  display: flex;
  flex-direction: column;
}
h4 {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 100px;
}
</style>
