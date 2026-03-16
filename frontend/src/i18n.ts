import { createI18n, I18n } from "vue-i18n";
import constants from "./constants";
import apiService from "@/services/api.service";

interface MyI18n extends I18n {
  locale: string;
}

function loadLocaleMessages() {
  const modules = import.meta.glob("./locales/*.json", {
    eager: true,
    import: "default",
  }) as Record<string, Record<string, unknown>>;
  const messages: Record<string, Record<string, unknown>> = {};
  for (const path in modules) {
    const matched = path.match(/([A-Za-z0-9-_]+)\.json$/i);
    if (matched && matched.length > 1) {
      messages[matched[1]] = modules[path];
    }
  }
  return messages;
}

const i18n = createI18n({
  locale: localStorage.getItem("locale") || constants.i18nDefaultLocale,
  legacy: false,
  globalInjection: true,
  fallbackLocale: constants.i18nFallbackLocale,
  messages: loadLocaleMessages(),
}) as MyI18n;

if (!localStorage.getItem("locale")) {
  apiService.getLocaleConfig().then((result) => {
    i18n.global.locale = result.locale || (constants.i18nDefaultLocale ?? "");
  });
}

export default i18n;
