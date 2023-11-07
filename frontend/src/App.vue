<!--
SPDX-License-Identifier: AGPL-3.0-or-later
Diveni - The Planing-Poker App
Copyright (C) 2022 AUME-Team 21/22, HTWG Konstanz
-->

<template>
  <div>
    <top-navigation-bar />
    <main>
      <feedback-button></feedback-button>
      <router-view />
    </main>
    <footer-bar />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import TopNavigationBar from "@/components/navigation/TopNavigationBar.vue";
import FooterBar from "@/components/navigation/FooterBar.vue";
import FeedbackButton from "@/components/FeedbackButton.vue";

export default Vue.extend({
  name: "App",
  components: { FeedbackButton, FooterBar, TopNavigationBar },
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
  color: var(--text-primary-color);
}
</style>
