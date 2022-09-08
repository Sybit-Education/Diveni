<template>
  <div class="rounded-avatar rounded-circle" :style="`background-color: ${member.hexColor}`">
    <div :id="'avatar' + member.name" class="text-center">
      <b-img
        :src="require(`@/assets/${member.avatarAnimal.toLowerCase()}.png`)"
        class="rounded-avatar__image"
      />
      <div v-if="showName">
        {{ member.name }}
      </div>
    </div>
    <b-tooltip :target="'avatar' + member.name" triggers="hover" top>{{ member.name }}</b-tooltip>
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
.rounded-avatar {
  width: 90px;
  height: 90px;
  padding: 8px;
  display: flex;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
}
.rounded-avatar__image {
  width: 45px;
}
.rounded-avatar__name {
  font-size: 1rem;
  font-weight: 600;
}
</style>
