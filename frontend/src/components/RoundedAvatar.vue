<template>
  <div
    class="rounded-avatar rounded-circle"
    :style="`background-color: ${member.hexColor}`"
    :class="mobile ? 'smallAvatar' : 'bigAvatar'"
  >
    <div :id="'avatar' + member.name" class="text-center">
      <b-img :src="require(`@/assets/${avatar}.png`)" class="rounded-avatar__image" />
      <div v-if="showName" class="rounded-avatar__label">
        {{ member.name }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import Member from "@/model/Member";
import Constants from "@/constants";

export default defineComponent({
  name: "RoundedAvatar",
  props: {
    member: { type: Object as PropType<Member>, required: true },
    showName: { type: Boolean, default: true },
    admin: { type: Boolean, default: true },
    mobile: { type: Boolean, default: false },
  },
  computed: {
    avatar() {
      if (this.member.avatarAnimal === undefined) {
        return Constants.getRandomAvatarAnimalAssetName();
      }
      return this.member?.avatarAnimal?.toLowerCase() ?? "fish";
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.rounded-avatar {
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
  font-size: medium;
  font-weight: bold;
  text-overflow: ellipsis;
  color: black;
}

.bigAvatar {
  width: 150px;
  height: 150px;
}

.smallAvatar {
  width: 100px;
  height: 100px;
}
</style>
