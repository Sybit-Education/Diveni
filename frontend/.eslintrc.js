module.exports = {
  root: true,
  env: {
    node: true,
  },
  extends: [
    'plugin:vue/recommended',
    "@vue/airbnb",
    "@vue/typescript/recommended",
  ],
  parserOptions: {
    ecmaVersion: 2020,
  },
  rules: {
    "no-console": process.env.NODE_ENV === "production" ? "warn" : "off",
    "no-debugger": process.env.NODE_ENV === "production" ? "warn" : "off",
    "quotes": [2, "single", { "avoidEscape": true }],
    "no-shadow": "off",
    "@typescript-eslint/no-shadow": ["error"],
    "noImplicitAny": 0,
    "allowJs": 0,
  },
};
