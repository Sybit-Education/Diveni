<template>
  <div :style="`background-color: ${member.hexColor}; width: 150px`" class="text-center">
    <img
      :src="require(`@/assets/${member.avatarAnimal.toLowerCase()}.png`)"
      :alt="member.avatarAnimal.toLowerCase() + ' image'"
      width="70"
    />
    <h4 class="m-2">
      <strong>{{ member.name }}</strong>
    </h4>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  props: {
    member: { type: Object, required: true },
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
