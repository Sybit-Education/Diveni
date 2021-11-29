module.exports = {
  root: true,
  env: {
    node: true,
  },
  extends: [
    'plugin:vue/recommended',
    '@vue/airbnb',
    '@vue/typescript/recommended',
  ],
  parserOptions: {
    ecmaVersion: 2020,
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    quotes: [2, 'single', { avoidEscape: true }],
    'no-shadow': 'off',
    'max-len': ['error', { code: 300 }],
    'vue/max-attributes-per-line': ['error', {
      singleline: {
        max: 10,
      },
      multiline: {
        max: 10,
      },
    }],

    '@typescript-eslint/no-shadow': ['error'],
    noImplicitAny: 0,
    allowJs: 0,
    'vue/no-unused-components': 0,
    'no-unused-components': 0,
  },
};
