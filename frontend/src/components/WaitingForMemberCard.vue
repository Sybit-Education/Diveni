<template>
  <div
    :style="cardStyles"
    class="pt-4 pt-sm-5 text-center mt-2 waiting-card-height"
  >
    <img src="../assets/icons/dog.svg" alt="dog" v-if="icon == 1" />
    <img src="../assets/icons/lion.svg" alt="dog" v-if="icon == 2" />
    <img src="../assets/icons/monkey.svg" alt="dog" v-if="icon == 3" />
    <h4 class="mt-2 pb-3 pb-sm-4 pb-md-5"><strong>{{ name }}</strong></h4>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'WaitingForMemberCard',
  props: {
    name: { type: String, required: true },
    color: { type: String, required: true },
    icon: { type: Number, required: true },
  },
  computed: {
    cardStyles() {
      return {
        'background-color': this.color,
        'border-radius': '100%',
      };
    },
  },
});
</script>
