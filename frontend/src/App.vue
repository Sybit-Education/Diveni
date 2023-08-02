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
@import './assets/variables.css';
main {
  min-height: calc(100vh - 10rem);
  background: linear-gradient(var(--background-color-primary), var(--background-color-secondary)); /**#BFDBF7 #2A3C24*/
  backdrop-filter: hue-rotate(120deg);
  color: var(--text-primary-color) !important;
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
