<template>
  <div>
    <input
      @change="toggleTheme"
      id="checkbox"
      type="checkbox"
      class="switch-checkbox"
    />
    <label for="checkbox" class="switch-label">
      <span>🌙</span>
      <span>☀️</span>
      <div
        class="switch-toggle"
        :class="{ 'switch-toggle-checked': userTheme === 'dark-theme' }"
      />
    </label>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  name: "ThemeToggleComponent",
  data() {
    return {
      userTheme: "light-theme"
    }
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
      window.dispatchEvent(new CustomEvent('user-theme-localstorage-changed', {
        detail: {
          storage: localStorage.getItem('user-theme')
        }
      }));
    },
  }
});
</script>

<style scoped>
.switch-checkbox {
  display: none;
}
.switch-toggle-checked {
  transform: translateX(calc(var(--element-size) * 0.625)) !important;
}
.switch-toggle {
  position: absolute;
  background-color: var(--background-color-primary);
  border-radius: 50%;
  left: 0rem;
  height: calc(var(--element-size) * 0.3);
  width: calc(var(--element-size) * 0.3);
  transform: translateX(0);
  transition: transform 0.3s ease, background-color 0.5s ease;
}
.switch-label {
  /* for width, use the standard element-size */
  width: 5.75rem;
  user-select: none;

  /* for other dimensions, calculate values based on it */
  border-radius: var(--element-size);
  border: calc(var(--element-size) * 0.025) solid var(--accent-color);
  font-size: calc(var(--element-size) * 0.2);
  height: calc(var(--element-size) * 0.35);
  padding-left: 5px;

  align-items: center;
  background: var(--text-primary-color);
  cursor: pointer;
  display: flex;
  position: relative;
  transition: background 0.5s ease;
  justify-content: space-between;
  z-index: 1;
}
</style>
