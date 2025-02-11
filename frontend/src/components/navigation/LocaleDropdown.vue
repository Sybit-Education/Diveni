<template>
  <ul style="list-style-type: none; padding: 0">
    <b-nav-item-dropdown :text="locales[currentLocale]" right>
      <ul style="list-style-type: disc">
        <b-dropdown-item
          v-for="(locale, key) in locales"
          :key="key"
          :active="currentLocale === key"
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
import { defineComponent, ref } from "vue";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "LocaleDropdown",
  setup() {
    const { t, locale } = useI18n();
    const currentLocale = ref(locale.value);

    const setLocale = (newLocale: string) => {
      locale.value = newLocale;
      currentLocale.value = newLocale;
      localStorage.setItem("locale", newLocale);
    };

    return {
      t,
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
      currentLocale,
      setLocale,
    };
  },
});
</script>
