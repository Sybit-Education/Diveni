<template>
  <div
    class="rounded-avatar rounded-circle"
    :class="admin ? 'smallAvatars' : 'bigAvatars'"
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
import Vue from "vue";

export default Vue.extend({
  name: "RoundedAvatar",
  props: {
    member: { type: Object, required: true },
    showName: { type: Boolean, default: true },
    admin: { type: Boolean, default: true},
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

.rounded-avatar {
  padding: 8px;
  display: flex;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
  overflow: hidden;
}

.smallAvatars{
  width: 100px;
  height: 100px;
}

.bigAvatars {
  width: 150px;
  height: 150px;
}
.rounded-avatar__image {
  width: 50px;
}

.rounded-avatar__label {
  font-size: large;
  font-weight: bold;
  text-overflow: ellipsis;
  color: black;
}
</style>
