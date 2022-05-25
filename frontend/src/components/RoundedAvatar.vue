<template>
  <div id="parent" class="rounded-circle" :style="`background-color: ${color}`">
    <div id="column" class="text-center">
      <img :src="require(`@/assets/${assetName}`)" width="44" />
      <div v-if="showName" id="name">
        {{ name }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "RoundedAvatar",
  props: {
    color: { type: String, required: true },
    assetName: { type: String, required: true },
    showName: { type: Boolean, required: true },
    name: { type: String, required: true },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#parent {
  width: 90px;
  height: 90px;
  padding-left: 8px;
  padding-bottom: 8px;
  padding-right: 8px;
  padding-top: 8px;
  display: flex;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
}
#name {
}
</style>
