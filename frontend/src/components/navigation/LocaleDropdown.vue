<template>
  <ul style="list-style-type: none; padding: 0">
    <b-nav-item-dropdown :text="locales[$i18n.locale]" right>
      <ul style="list-style-type: disc">
        <b-dropdown-item
          v-for="(locale, key) in locales"
          :key="key"
          :active="$i18n.locale === key ? true : false"
          class="text-light"
          @click="setLocale(key)"
        >
          {{ locale }}
        </b-dropdown-item>
      </ul>
      <b-dropdown-divider></b-dropdown-divider>
      <b-dropdown-item href="https://crowdin.com/project/diveni">
        {{ t("general.licenses.translations") }}
      </b-dropdown-item>
    </b-nav-item-dropdown>
  </ul>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "LocaleDropdown",
  setup() {
    const { t } = useI18n();
    return { t };
  },
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
    setLocale(locale) {
      this.$i18n.locale = locale;
      localStorage.setItem("locale", locale);
    },
  },
});
</script>
