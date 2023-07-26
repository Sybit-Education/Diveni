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
  background: linear-gradient(var(--background-color-primary), var(--background-color-secondary)); /**#BFDBF7 #2A3C24*/
  color: var(--text-primary-color) !important;
}

:root {
  --background-color-primary: #e2e2e2;/*#2D93AD */
  --background-color-secondary: #999999;/*#d6771e */
  --pictureGradientEnd: rgba(19, 44, 20, 0.5);
  --accent-color: #cacaca;
  --text-primary-color: #222;
  --element-size: 6rem;
  --startButton: #4db971;
  --startButtonHovered: #67e68f;
  --joinButton: #e27712;
  --joinButtonHovered: #f1a054;
  --reconnectButton: #0ec5c5;
  --reconnectButtonHovered: #40ecec;
  --landingPageCardsBackground: #e4e4e4;
  --preparePageMainColor:#b9934d;
  --preparePageInActiveTab: #ffffff;
  --preparePageInActiveTabHover: #3daeb6;
  --preparePageBorderTab: grey;
  --preparePageImportUserStoriesButton: #30a858;
  --linkColor: #254bb3;
  --topNavigationBarColor: rgba(200, 200, 200, 0.75);
  --blurBackGroundColor: rgba(80, 80, 80, 0.219);
  --popUpMenu: rgb(143, 143, 143);
}

/* Define styles for the root window with dark - mode preference */
:root.dark-theme {
  --background-color-primary: #0e041a; /* #230903 #564787  #2C1320 #002E2C*/
  --background-color-secondary: #160e20; /**#9D6381 35605A 820B8A #b81484*/
  --pictureGradientEnd: rgba(44, 19, 32, 0.5);
  --accent-color: rgba(34, 34, 34, 0.863);
  --text-primary-color: #ffffff;
  --startButton: #056921;
  --startButtonHovered: #08a134;
  --joinButton: #866700;
  --joinButtonHovered: #aa8200;
  --reconnectButton: #067979;
  --reconnectButtonHovered: #07a0a0;
  --landingPageCardsBackground: #525252;
  --preparePageMainColor:#997e26; /*#6c1a6e;*/
  --preparePageInActiveTab: #666565;
  --preparePageInActiveTabHover: #328c92;
  --preparePageBorderTab: white;
  --preparePageImportUserStoriesButton: #7e5a0c;
  --linkColor: #00ffdd;
  --topNavigationBarColor: rgba(34, 34, 34, 0.863);/*rgba(45, 41, 41, 0.75) rgb(79, 69, 69, 0.88)*/
  --blurBackGroundColor: #50505069;
  --popUpMenu: rgb(201, 201, 201);
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
/* Landing Page */
.newSessionCard .landingPageCardButton{
  background-color: var(--startButton);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.newSessionCard .landingPageCardButton:hover{
  background-color: var(--startButtonHovered);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.newSessionCard .card-footer {
  background-color: #52173100; /* So the Footer does not overflow */
}

.joinSessionCard .landingPageCardButton{
  background-color: var(--joinButton);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.joinSessionCard .landingPageCardButton:hover{
  background-color: var(--joinButtonHovered);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.joinSessionCard .card-footer {
  background-color: #52173100; /* So the Footer does not overflow */
}

.reconnectSessionCard .landingPageCardButton {
  background-color: var(--reconnectButton);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.reconnectSessionCard .landingPageCardButton:hover {
  background-color: var(--reconnectButtonHovered);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.reconnectSessionCard .card-footer {
  background-color: #52173100; /* So the Footer does not overflow */
}
/* Prepare Page */
.selectedTab  {
  background-color: var(--preparePageMainColor) !important;
}

.selectedTextColor {
  color: var(--text-primary-color) !important;
}

.notSelectedTab {
  background-color: var(--preparePageInActiveTab) !important;
  border-color: var(--preparePageBorderTab) !important;
}

.notSelectedTab:hover {
  background-color: var(--preparePageInActiveTabHover) !important;
  border-color: var(--preparePageBorderTab) !important;
}

.notSelectedTextColor {
  color: var(--text-primary-color) !important;
}

.dropDownLink {
  color: var(--text-primary-color) !important;
}
.containerClass {
  background-color: var(--landingPageCardsBackground) !important;
  color: var(--text-primary-color) !important;
}

.dropdown-item {
  color: var(--text-primary-color) !important;
}

.dropdown-item:hover {
  color: black !important;
}

.dropdown-item.active {
  background-color: var(--startButton) !important;
}

a.btn.joinButton {
  background-color: var(--joinButton);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

a.btn.joinButton:hover {
  background-color: var(--joinButtonHovered);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

a.btn.startButton {
  background-color: var(--startButton);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

a.btn.startButton:hover {
  background-color: var(--startButtonHovered);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.btn.startButton {
  background-color: var(--startButton);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.btn.startButton:hover {
  background-color: var(--startButtonHovered);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.btn.startButton:disabled {
  background-color: var(--preparePageInActiveTab);
  border-radius: 2rem;
  color: var(--text-primary-color);
}

.btn.dropdown-toggle {
  color: var(--text-primary-color);
  background-color: var(--preparePageMainColor);
}

.btn.dropdown-toggle:hover {
  color: var(--text-primary-color);
  background-color: var(--joinButtonHovered);
}

.btn-secondary:not(:disabled):not(.disabled):active, .btn-secondary:not(:disabled):not(.disabled).active, .show > .btn-secondary.dropdown-toggle {
  color: var(--text-primary-color) !important ;
  background-color: var(--preparePageMainColor) !important;
}
.btn-secondary:focus {
  color: var(--text-primary-color) !important ;
  background-color: var(--preparePageMainColor) !important;
}

.a.dropdown-item {
  color: var(--text-primary-color) !important;
}

.a.dropdown-item:hover {
  color: black !important;
}

.a.dropdown-item.active {
  background-color: green !important;
}
.dropdown-menu {
  background-color: var(--topNavigationBarColor) !important;
  border-color: var(--text-primary-color) !important;
}
</style>
