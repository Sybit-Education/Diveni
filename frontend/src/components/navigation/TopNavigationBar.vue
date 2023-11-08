<template>
  <b-navbar class="top-navigation" type="light" fixed="top" sticky>
    <b-navbar-brand class="top-navigation__title" to="/">
      <b-img src="/img/icons/logo.svg" class="top-navigation__nav-logo" />
      {{ $t("page.landing.productTitle") }}
    </b-navbar-brand>
    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav class="ml-auto">
        <b-nav-form>
          <b-button :to="{ name: 'PrepareSessionPage' }" variant="primary" class="mr-2">
            {{ $t("page.landing.meeting.new.buttons.start.label") }}
          </b-button>
        </b-nav-form>
        <b-nav-form>
          <b-button :to="{ name: 'JoinPage' }" variant="secondary" class="mr-2">
            {{ $t("page.landing.meeting.join.buttons.start.label") }}
          </b-button>
        </b-nav-form>
        <b-nav-item href="https://github.com/Sybit-Education/Diveni" target="_blank">
          <BIconGithub class="github-logo py-2"></BIconGithub>
        </b-nav-item>
        <b-nav-text>
          <theme-toggle class="my-2" />
        </b-nav-text>
        <b-nav-item>
          <locale-dropdown />
        </b-nav-item>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script lang="ts">
import Vue from "vue";
import LocaleDropdown from "@/components/navigation/LocaleDropdown.vue";
import { BIconGithub } from "bootstrap-vue";
import ThemeToggle from "@/components/actions/ThemeToggle.vue";

export default Vue.extend({
  name: "TopNavigationBar",
  components: { LocaleDropdown, BIconGithub, ThemeToggle },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.top-navigation {
  background-color: var(--topNavigationBarColor);

  &__nav-logo {
    height: 3rem;
  }
  &__title {
    font-size: 2.5rem;
    font-weight: 700;
  }

  .github-logo {
    color: var(--text-primary-color);
    width: 2.75rem;
    height: 2.75rem;
  }

  .btn-secondary:not(:disabled):not(.disabled),
  .show > .btn-secondary.dropdown-toggle {
    background-color: var(--blurredColour7);
  }
}
</style>
