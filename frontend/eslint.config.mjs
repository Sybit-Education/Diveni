import js from "@eslint/js";
import globals from "globals";
import pluginVue from "eslint-plugin-vue";
import prettierConfig from "@vue/eslint-config-prettier";
import { defineConfigWithVueTs, vueTsConfigs } from "@vue/eslint-config-typescript";

export default defineConfigWithVueTs(
  {
    ignores: ["dist/**", "node_modules/**"],
    linterOptions: {
      reportUnusedDisableDirectives: "error",
    },
  },
  js.configs.recommended,
  pluginVue.configs["flat/recommended"],
  vueTsConfigs.recommended,
  prettierConfig,
  {
    files: ["src/**/*.{ts,vue}"],
    languageOptions: {
      ecmaVersion: "latest",
      globals: {
        ...globals.node,
      },
    },
    rules: {
      "vue/no-required-prop-with-default": "off",
      "no-console": "off",
      "no-debugger": "off",
      "@typescript-eslint/no-non-null-assertion": "off",
      "@typescript-eslint/no-unused-expressions": "off",
      "prettier/prettier": ["warn", { endOfLine: "auto", trailingComma: "es5" }],
    },
  },
);
