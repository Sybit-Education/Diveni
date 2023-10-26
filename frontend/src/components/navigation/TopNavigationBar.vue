<template>
  <b-navbar toggleable="md" class="top-navigation" fixed="top" sticky>
    <b-navbar-brand class="top-navigation__title" to="/">
      <b-img src="/img/icons/logo.svg" class="top-navigation__nav-logo" />
      {{ $t("page.landing.productTitle") }}
    </b-navbar-brand>
    <theme-toggle-component />
    <b-navbar-nav class="ml-auto">
      <b-form>
        <b-button :to="{ name: 'PrepareSessionPage' }" class="startButton px-2 mr-2"
          >New Session</b-button
        >
      </b-form>
      <b-form>
        <b-button :to="{ name: 'JoinPage' }" class="joinButton px-2 mr-2">
          {{ $t("page.landing.meeting.join.buttons.start.label") }}</b-button
        >
      </b-form>
      <b-form class="px-2 mr-2">
        <a id="gitHubLink" href="https://github.com/Sybit-Education/Diveni" target="_blank">
          <BIconGithub id="gitHubLogo"></BIconGithub>
        </a>
      </b-form>
      <locale-dropdown />
    </b-navbar-nav>
  </b-navbar>
</template>

<script lang="ts">
import Vue from "vue";
import LocaleDropdown from "@/components/navigation/LocaleDropdown.vue";
import { BIconGithub } from "bootstrap-vue";
import ThemeToggleComponent from "@/components/ThemeToggleComponent.vue";

export default Vue.extend({
  name: "TopNavigationBar",
  components: { LocaleDropdown, BIconGithub, ThemeToggleComponent },
});
</script>

<style>
/* TOP Navigation Bar */

.top-navigation {
  background-color: var(--topNavigationBarColor);
}
.top-navigation__nav-logo {
  height: 3rem;
}
.top-navigation__title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary-color) !important;
}

#gitHubLogo {
  width: 40px;
  height: 40px;
}

#gitHubLink {
  color: var(--text-primary-color);
}
</style>
