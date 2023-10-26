<template>
  <div class="theme-toggle">
    <input id="checkbox" type="checkbox" class="theme-toggle__checkbox" @change="toggleTheme" />
    <label for="checkbox" class="theme-toggle__label">
      <span>🌙</span>
      <span>☀️</span>
      <div class="theme-toggle__toggle" :class="{ 'theme-toggle__toggle-checked': userTheme === 'dark-theme' }" />
    </label>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  name: "ThemeToggleComponent",
  data() {
    return {
      userTheme: "light-theme",
    };
  },
  created() {
    if (localStorage.getItem("user-theme")) {
      this.setTheme(localStorage.getItem("user-theme"));
    } else {
      this.setTheme(this.userTheme);
    }
  },
  methods: {
    toggleTheme() {
      const activeTheme = localStorage.getItem("user-theme");
      if (activeTheme === "light-theme") {
        this.setTheme("dark-theme");
      } else {
        this.setTheme("light-theme");
      }
    },
    setTheme(theme) {
      localStorage.setItem("user-theme", theme);
      this.userTheme = theme;
      document.documentElement.className = theme;
      window.dispatchEvent(
        new CustomEvent("user-theme-localstorage-changed", {
          detail: {
            storage: localStorage.getItem("user-theme"),
          },
        })
      );
    },
  },
});
</script>

<style scoped lang="scss">
.theme-toggle {
  $element-size: 1.5rem;
  margin-left: 0.25rem;
  margin-right: 0.25rem;

  &__checkbox {
    display: none;
  }

  &__toggle-checked {
    transform: translateX(calc($element-size * 1)) !important;
  }

  &__toggle {
    position: absolute;
    background-color: var(--topNavigationBarColor);
    border-radius: 50%;
    left: 0;
    height: calc($element-size);
    width: calc($element-size);
    transform: translateX(0);
    transition: transform 0.3s ease, background-color 0.5s ease;
  }

  &__label {
    height: calc($element-size);
    width: calc($element-size * 2);
    user-select: none;

    /* for other dimensions, calculate values based on it */
    border-radius: var(--buttonShape);
    border-color: var(--accent-color);
    background-color: var(--text-primary-color);
    font-size: calc($element-size * 0.75);

    align-items: center;

    cursor: pointer;
    display: flex;
    position: relative;
    transition: background 0.5s ease;
    justify-content: space-between;
    z-index: 1;
  }
}
</style>
