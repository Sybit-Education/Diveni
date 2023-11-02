<template>
  <b-navbar class="top-navigation" type="light" fixed="top" sticky>
    <b-navbar-brand class="top-navigation__title" to="/">
      <b-img src="/img/icons/logo.svg" class="top-navigation__nav-logo" />
      {{ t("page.landing.productTitle") }}
    </b-navbar-brand>
    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav class="ml-auto">
        <b-button :to="{ name: 'PrepareSessionPage' }" variant="primary" class="mr-2"
          >New Session</b-button
        >
        <b-button :to="{ name: 'JoinPage' }" class="mr-2">
          {{ t("page.landing.meeting.join.buttons.start.label") }}
        </b-button>
        <a href="https://github.com/Sybit-Education/Diveni" target="_blank">
          <BIconGithub class="github-logo p-2"></BIconGithub>
        </a>
        <theme-toggle class="p-2" />
        <locale-dropdown />
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";
import LocaleDropdown from "@/components/navigation/LocaleDropdown.vue";
import { BIconGithub } from "bootstrap-vue";
import ThemeToggle from "@/components/actions/ThemeToggle.vue";

export default defineComponent({
  name: "TopNavigationBar",
  components: { LocaleDropdown, BIconGithub, ThemeToggle },
  setup() {
    const { t } = useI18n();
    return { t };
  },
});
</script>

<style scoped lang="scss">
/* TOP Navigation Bar */

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
}
</style>
