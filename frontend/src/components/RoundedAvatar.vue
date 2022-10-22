<template>
  <div
    class="rounded-avatar rounded-circle"
    :style="`background-color: ${member.hexColor}`"
  >
    <div :id="'avatar' + member.name" class="text-center">
      <b-img
        :src="require(`@/assets/${member.avatarAnimal.toLowerCase()}.png`)"
        class="rounded-avatar__image"
      />
      <div v-if="showName" class="rounded-avatar__label">
        {{ member.name }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
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
  width: 100px;
  height: 100px;
  padding: 8px;
  display: flex;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
  overflow: hidden;
}
.rounded-avatar__image {
  width: 50px;
}

.rounded-avatar__label {
  font-size: 0.75rem;
  font-weight: 400;
  text-overflow: ellipsis;
}
</style>
