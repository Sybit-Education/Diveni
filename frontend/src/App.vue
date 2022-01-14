<template>
  <b-container>
    <router-view />
    <locale-dropdown></locale-dropdown>
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import LocaleDropdown from "./components/LocaleDropdown.vue";

export default Vue.extend({
  name: "App",
  components: { LocaleDropdown },
  data: () => ({
    //
  }),
  mounted() {
    document.title = "Planning-Poker";
  },
});
</script>
