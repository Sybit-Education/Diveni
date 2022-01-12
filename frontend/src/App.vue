<template>
  <b-container>
    <router-view />
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "App",
  data: () => ({
    //
  }),
  mounted() {
    document.title = "Planning-Poker";
  },
});
</script>
