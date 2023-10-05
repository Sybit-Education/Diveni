import Vue from "vue";
import VueI18n from "vue-i18n";
import constants from "./constants";
import apiService from "@/services/api.service";

Vue.use(VueI18n);

function loadLocaleMessages() {
  const locales = require.context("./locales", true, /[A-Za-z0-9-_,\s]+\.json$/i);
  const messages = {};
  locales.keys().forEach((key) => {
    const matched = key.match(/([A-Za-z0-9-_]+)\./i);
    if (matched && matched.length > 1) {
      const locale = matched[1];
      messages[locale] = locales(key);
    }
  });
  return messages;
}

const i18n = new VueI18n({
  locale: localStorage.getItem("locale") || constants.i18nDefaultLocale,
  fallbackLocale: constants.i18nFallbackLocale,
  messages: loadLocaleMessages(),
});

if (!localStorage.getItem("locale")) {
  apiService.getLocaleConfig().then((result) => {
    i18n.locale = result.locale || constants.i18nDefaultLocale;
  });
}

export default i18n;
