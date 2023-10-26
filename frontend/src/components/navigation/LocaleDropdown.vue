<template>
  <b-nav-item-dropdown :text="locales[$i18n.locale]" right>
    <b-dropdown-item
      v-for="(locale, key) in locales"
      :key="key"
      :active="$i18n.locale === key ? true : false"
      class="text-light"
      @click="setLocale(key)"
      >{{ locale }}</b-dropdown-item
    >
    <hr />
    <b-dropdown-item href="https://crowdin.com/project/diveni">
      {{ $t("general.licenses.translations") }}
    </b-dropdown-item>
  </b-nav-item-dropdown>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "LocaleDropdown",
  data() {
    return {
      locales: {
        de: "Deutsch",
        en: "English",
        fr: "Français",
        it: "Italiano",
        es: "Español",
        pl: "Polski",
        pt: "Português",
        uk: "українська",
      },
    };
  },
  methods: {
    setLocale(locale: string) {
      this.$i18n.locale = locale;
      localStorage.setItem("locale", locale);
    },
  },
});
</script>
