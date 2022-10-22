import Vue from "vue";
import { createI18n } from "vue-i18n";
import constants from "./constants";


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

const locale = localStorage.getItem("locale") || constants.i18nLocale || "de";

export const i18n = createI18n({
  legacy: false,
  locale: locale,
  fallbackLocale: constants.i18nFallbackLocale || "de",
  messages: loadLocaleMessages(),
});
