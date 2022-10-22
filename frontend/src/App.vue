<!--
SPDX-License-Identifier: AGPL-3.0-or-later
Diveni - The Planing-Poker App
Copyright (C) 2022 AUME-Team 21/22, HTWG Konstanz
-->

<template>
  <div>
    <top-navigation-bar />
    <main>
      <router-view />
    </main>
    <footer-bar />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import TopNavigationBar from "@/components/navigation/TopNavigationBar.vue";
import FooterBar from "@/components/navigation/FooterBar.vue";

export default defineComponent({
  name: "App",
  components: { FooterBar, TopNavigationBar },
  mounted() {
    document.title = "DIVENI";
  },
});
</script>
<style>
main {
  min-height: calc(100vh - 10rem);
}
</style>
