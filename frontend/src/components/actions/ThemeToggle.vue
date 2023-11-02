<template>
  <div class="theme-toggle">
    <input id="checkbox" type="checkbox" class="theme-toggle__checkbox" @change="toggleTheme" />
    <label for="checkbox" class="theme-toggle__label">
      <span>🌙</span>
      <span>☀️</span>
      <div
        class="theme-toggle__toggle"
        :class="{ 'theme-toggle__toggle-checked': userTheme === 'dark' }"
      />
    </label>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
  name: "ThemeToggle",
  data() {
    return {
      userTheme: "light",
    };
  },
  mounted() {
    if (localStorage.getItem("user-theme")) {
      this.setTheme(localStorage.getItem("user-theme"));
    } else {
      this.setTheme(this.userTheme);
    }
  },
  methods: {
    toggleTheme() {
      const activeTheme = localStorage.getItem("user-theme");
      if (activeTheme === "light") {
        this.setTheme("dark");
      } else {
        this.setTheme("light");
      }
    },
    setTheme(theme: string | null) {
      if (theme === null) {
        theme = "light";
      }
      localStorage.setItem("user-theme", theme);
      this.userTheme = theme;
      document.documentElement.className = theme;
      if (theme === "dark") {
        this.darkMode();
      } else {
        this.lightMode();
      }
      window.dispatchEvent(
        new CustomEvent("user-theme-localstorage-changed", {
          detail: {
            storage: localStorage.getItem("user-theme"),
          },
        })
      );
    },
    /**
     * @function darkmode
     * @summary: firstly, checks if browser local storage has 'user-theme' key
     * if key has 'dark' value then changes the theme to 'dark mode'
     * Basically, replaces/toggles every CSS class that has '-light' class with '-dark'
     */
    darkMode() {
      document.querySelectorAll(".bg-light").forEach((element) => {
        element.className = element.className.replace(/-light/g, "-dark");
      });
      document.querySelectorAll(".navbar-light").forEach((element) => {
        element.className = element.className.replace(/-light/g, "-dark");
      });
      document.body.classList.add("bg-dark");

      if (document.body.classList.contains("text-dark")) {
        document.body.classList.replace("text-dark", "text-light");
      } else {
        document.body.classList.add("text-light");
      }
    },

    /**
     * @function lightmode
     * @summary: check whether the switch is on (checked) or not.
     * If the switch is on then set 'lightSwitch' local storage key and call @function darkmode
     * If the switch is off then it is light mode so, switch the theme and
     *  remove 'lightSwitch' key from local storage
     */
    lightMode() {
      document.querySelectorAll(".bg-dark").forEach((element) => {
        element.className = element.className.replace(/-dark/g, "-light");
      });
      document.querySelectorAll(".navbar-dark").forEach((element) => {
        element.className = element.className.replace(/-dark/g, "-light");
      });
      document.body.classList.replace("text-light", "text-dark");
    },
  },
});
</script>

<style scoped lang="scss">
@import "@/assets/style/variables.scss";
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
    opacity: 1;
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
    border-radius: $border-radius;
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
