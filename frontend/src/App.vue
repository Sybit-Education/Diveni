<!--
SPDX-License-Identifier: AGPL-3.0-or-later
Diveni - The Planing-Poker App
Copyright (C) 2022 AUME-Team 21/22, HTWG Konstanz
-->

<template>
  <b-container>
    <router-view />
    <div class="d-flex justify-content-between">
      <locale-dropdown></locale-dropdown>
      <about-dropdown></about-dropdown>
    </div>
  </b-container>
</template>

<script lang="ts">
import Vue from "vue";
import LocaleDropdown from "./components/LocaleDropdown.vue";
import AboutDropdown from "./components/AboutDropdown.vue";

export default Vue.extend({
  name: "App",
  components: { LocaleDropdown, AboutDropdown },
  data: () => ({
    //
  }),
  mounted() {
    document.title = "DIVENI";
  },
});
</script>
