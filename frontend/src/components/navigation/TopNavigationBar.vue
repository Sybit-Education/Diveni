<template>
  <b-navbar class="top-navigation navbar-light" fixed="top">
    <b-navbar-brand class="top-navigation__title" to="/">
      <b-img src="/img/icons/logo.svg" width="48" height="48" alt="Diveni Logo" />
      {{ t("page.landing.productTitle") }}
    </b-navbar-brand>
    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav class="ms-auto">
        <li class="nav-item d-flex align-items-center">
          <b-button :to="{ name: 'PrepareSessionPage' }" variant="primary" class="me-2">
            {{ t("page.landing.meeting.new.buttons.start.label") }}
          </b-button>
        </li>
        <li class="nav-item d-flex align-items-center">
          <b-button :to="{ name: 'JoinPage' }" variant="secondary" class="me-2">
            {{ t("page.landing.meeting.join.buttons.start.label") }}
          </b-button>
        </li>
        <b-nav-item href="https://github.com/Sybit-Education/Diveni" target="_blank" class="me-1">
          <i class="bi bi-github github-logo"></i>
        </b-nav-item>
        <li class="nav-item d-flex align-items-center">
          <theme-toggle class="my-2" />
        </li>
        <b-nav-item>
          <locale-dropdown />
        </b-nav-item>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";
import LocaleDropdown from "@/components/navigation/LocaleDropdown.vue";
import ThemeToggle from "@/components/actions/ThemeToggle.vue";

export default defineComponent({
  name: "TopNavigationBar",
  components: { LocaleDropdown, ThemeToggle },
  setup() {
    const { t } = useI18n();
    return { t };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.top-navigation {
  background-color: var(--topNavigationBarColor);

  &__title {
    font-size: 2.5rem;
    font-weight: 700;
  }

  .github-logo {
    color: var(--text-primary-color);
    font-size: 1.75rem;
  }
}
</style>
