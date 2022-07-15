<template>
  <div id="parent" class="rounded-circle" :style="`background-color: ${member.hexColor}`">
    <div id="column" class="text-center">
      <img :src="require(`@/assets/${member.avatarAnimal.toLowerCase()}.png`)" width="44" />
      <div v-if="showName" id="name">
        {{ member.name }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "RoundedAvatar",
  props: {
    member: { type: Object, required: true },
    showName: { type: Boolean, default: true },
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
</style>
