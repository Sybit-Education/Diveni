<template>
    <b-col
    :style="backgroundcolorIsSet ? 'background-color: #aaaaaa' : 'background-color: #ffffff'"
    class="p-3 mb-2 text-white rounded"
    @click="doTheThing()"> {{ cardName }} </b-col>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'DesktopCard',
  props: {
    cardName: { type: Number, required: true },
  },
  data() {
    return {
      backgroundcolorIsSet: true,
    };
  },
  methods: {
    doTheThing() {
      this.backgroundcolorIsSet = !this.backgroundcolorIsSet;
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#text {
  font-size: 20px;
  font-weight: 500;
}
</style>
