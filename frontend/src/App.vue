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
import Vue from "vue";
import TopNavigationBar from "@/components/navigation/TopNavigationBar.vue";
import FooterBar from "@/components/navigation/FooterBar.vue";

export default Vue.extend({
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
  background: linear-gradient(var(--background-color-primary), var(--background-color-secondary)) /**#BFDBF7 #2A3C24*/
}

:root {
  --background-color-primary: #2D93AD;
  --background-color-secondary: #d6771e;
  --pictureGradientEnd: rgba(19, 44, 20, 0.5);
  --accent-color: #cacaca;
  --text-primary-color: #222;
  --element-size: 6rem;
  --startButton: #4db971;
  --startButtonHovered: #54be76;
  --joinButton: #d6771e;
  --joinButtonHovered: #cf8642;
  --reconnectButton: #0ec5c5;
  --reconnectButtonHovered: #54cece;
  --landingPageCardsBackground: #e4e4e4;
  --preparePageMainColor:#4db971;
  --preparePageInActiveTab: #ffffff;
  --preparePageBorderTab: grey;
  --preparePageImportUserStoriesButton: #30a858;
}

/* Define styles for the root window with dark - mode preference */
:root.dark-theme {
  --background-color-primary: #002E2C; /* #230903 #564787  #2C1320 #002E2C*/
  --background-color-secondary: #0884c4; /**#9D6381 35605A 820B8A #b81484*/
  --pictureGradientEnd: rgba(44, 19, 32, 0.5);
  --accent-color: #4b0e57;
  --text-primary-color: #ffffff;
  --startButton: #081b72;
  --startButtonHovered: #011158;
  --joinButton: #6c1a6e;
  --joinButtonHovered: #5f0861;
  --reconnectButton: rgb(143, 13, 99);
  --reconnectButtonHovered: rgb(92, 4, 63);
  --landingPageCardsBackground: #525252;
  --preparePageMainColor:#661b68; /*#6c1a6e;*/
  --preparePageInActiveTab: #666565;
  --preparePageBorderTab: white;
  --preparePageImportUserStoriesButton: #7e5a0c;
}

p {
  color: var(--text-primary-color);
}

h1 {
  color: var(--text-primary-color);
}

h4 {
  color: var(--text-primary-color);
}
</style>
