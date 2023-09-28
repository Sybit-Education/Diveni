<template>
  <b-navbar toggleable="md" class="top-navigation" fixed="top" sticky>
    <b-navbar-brand class="top-navigation__title" to="/">
      <b-img src="/img/icons/logo.svg" class="top-navigation__nav-logo"/>
      {{ $t("page.landing.productTitle") }}
    </b-navbar-brand>
    <b-navbar-nav class="ml-auto">
      <b-form>
        <b-button :to="{ name: 'JoinPage' }" variant="success" class="px-2 mr-2">
          {{ $t("page.landing.meeting.join.buttons.start.label") }}</b-button
          >
      </b-form>
      <b-form>
        <b-button :to="{ name: 'PrepareSessionPage' }" class="px-2 mr-2">New Session</b-button>
      </b-form>
      <b-form>
        <b-button :to="{name: 'WhatsNewPage'}"
          variant="outline-info">
          {{ $t("page.landing.news.buttons.info.label") }}
        </b-button>
      </b-form>
      <b-form class="px-2 mr-2">
        <a href="https://github.com/Sybit-Education/Diveni" target="_blank">
          <img :src="require('./images/GitHub-Mark-32px.png')" height="40px" width="40px"/>
        </a>
      </b-form>
      <locale-dropdown />
    </b-navbar-nav>
  </b-navbar>
</template>

<script lang="ts">
import Vue from "vue";
import LocaleDropdown from "@/components/navigation/LocaleDropdown.vue";
import WhatsNewPage from "@/views/WhatsNewPage.vue";

export default Vue.extend({
  name: "TopNavigationBar",
  computed: {
    WhatsNewPage() {
      return WhatsNewPage
    }
  },
  components: { LocaleDropdown },
});
</script>

<style scoped>
.top-navigation {
  background-color: rgba(200, 200, 200, 0.75);
}
.top-navigation__nav-logo {
  height: 3rem;
}
.top-navigation__title {
  font-size: 2.5rem;
  font-weight: 700;
}
</style>
