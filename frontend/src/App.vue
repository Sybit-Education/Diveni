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
<style lang="scss">
@import "./assets/style/variables.scss";
main {
  min-height: calc(100vh - 10rem);
  background:
  /* Top */ radial-gradient(
      farthest-corner at 500px 500px,
      var(--blurredColour1) 0.1%,
      rgba(255, 255, 255, 0) 26%
    ),
    radial-gradient(
      farthest-corner at 500px 500px,
      var(--blurredColour1) 0.1%,
      rgba(255, 255, 255, 0) 26%
    ),
    radial-gradient(farthest-corner at 7% 7%, var(--blurredColour2) 0.1%, rgba(255, 255, 255, 0) 8%),
    radial-gradient(
      farthest-corner at 17% 40px,
      var(--blurredColour3) 1%,
      rgba(255, 255, 255, 0) 16%
    ),
    radial-gradient(
      farthest-corner at 17% 40px,
      var(--blurredColour3) 1%,
      rgba(255, 255, 255, 0) 16%
    ),
    radial-gradient(
      farthest-corner at 36% 7%,
      var(--blurredColour4) 0.1%,
      rgba(255, 255, 255, 0) 18%
    ),
    radial-gradient(
      farthest-corner at 36% 7%,
      var(--blurredColour4) 0.1%,
      rgba(255, 255, 255, 0) 18%
    ),
    /* Bottom */
      radial-gradient(
        farthest-corner at 95% 42%,
        var(--blurredColour5) 0.1%,
        rgba(255, 255, 255, 0) 26%
      ),
    radial-gradient(
      farthest-corner at 0% 58%,
      var(--blurredColour5) 0.1%,
      rgba(255, 255, 255, 0) 22%
    ),
    radial-gradient(
      circle farthest-corner at 65% 95%,
      var(--blurredColour3) 0.1%,
      rgba(255, 255, 255, 0) 13%
    ),
    radial-gradient(
      farthest-corner at 100% 87%,
      var(--blurredColour3) 0.1%,
      rgba(255, 255, 255, 0) 13%
    ),
    radial-gradient(
      farthest-corner at 84% 82%,
      var(--blurredColour4) 0.1%,
      rgba(255, 255, 255, 0) 13%
    ),
    radial-gradient(
      circle farthest-corner at 84% 82%,
      var(--blurredColour6) 0.1%,
      rgba(255, 255, 255, 0) 13%
    );
  background-color: var(--background-color-primary);
  backdrop-filter: blur(5px);
  color: var(--text-primary-color) !important;
}

.reconnectSessionCard .landingPageCardButton {
  background-color: var(--reconnectButton);
  border-radius: var(--buttonShape);
}

.reconnectSessionCard .landingPageCardButton:hover {
  background-color: var(--reconnectButtonHovered);
}

.reconnectSessionCard .landingPageCardButton:focus {
  background-color: var(--reconnectButtonHovered) !important;
}

.reconnectSessionCard .card-footer {
  background-color: #52173100; /* So the Footer does not overflow */
}
/* Prepare Page */
.selectedTab {
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
}

.notSelectedTextColor {
  color: var(--text-primary-color) !important;
}

/*End of prepare Page */

a.btn.joinButton {
  background-color: var(--joinButton);
}

a.btn.joinButton:hover {
  background-color: var(--joinButtonHovered);
}

a.btn.startButton {
  background-color: var(--startButton);
}

a.btn.startButton:hover {
  background-color: var(--startButtonHovered);
}

a.btn.startButton:focus {
  background-color: var(--startButtonHovered) !important;
}

.btn.dropdown-toggle {
  /* User Estimation Button */
  color: var(--text-primary-color);
  background-color: var(--joinButton);
}

.btn.dropdown-toggle:active {
  /* User Estimation Button hold */
  background-color: var(--joinButtonHovered) !important;
  color: var(--text-primary-color) !important;
}

.btn.dropdown-toggle:hover {
  /* User Estimation Button Hover */
  background-color: var(--joinButtonHovered);
  color: var(--text-primary-color);
}

.btn-secondary:not(:disabled):not(.disabled).active,
.show > .btn-secondary.dropdown-toggle {
  /* User Estimation Button clicked */
  background-color: var(--joinButton) !important;
  color: var(--text-primary-color) !important;
}

.btn-secondary:focus {
  /* User Estimation Button after click & add User Story Button on click hold*/
  background-color: var(--joinButtonHovered) !important;
  color: var(--text-primary-color) !important;
}

</style>
