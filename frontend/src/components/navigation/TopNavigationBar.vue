<template>
  <b-navbar toggleable="md" class="top-navigation">
    <b-navbar-brand class="top-navigation__title" to="/">
      <b-img src="/img/icons/logo-128x128.png" class="top-navigation__nav-logo" />
      {{ $t("page.landing.productTitle") }}
    </b-navbar-brand>
    <b-navbar-nav class="ml-auto">
      <locale-dropdown />
    </b-navbar-nav>
  </b-navbar>
</template>

<script lang="ts">
import Vue from "vue";
import LocaleDropdown from "@/components/navigation/LocaleDropdown.vue";

export default Vue.extend({
  name: "TopNavigationBar",
  components: { LocaleDropdown },
});
</script>

<style scoped>
.top-navigation__nav-logo {
  height: 4rem;
}
.top-navigation__title {
  font-size: 2.5rem;
  font-weight: 700;
}
</style>
