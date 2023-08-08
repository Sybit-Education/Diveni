<template>
  <b-navbar toggleable="md" class="top-navigation" fixed="top" sticky>
    <b-navbar-brand class="top-navigation__title" style="color: var(--text-primary-color);" to="/">
      <b-img src="/img/icons/logo.svg" class="top-navigation__nav-logo"/>
      {{ $t("page.landing.productTitle") }}
    </b-navbar-brand>
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
      ></div>
    </label>
    <b-navbar-nav class="ml-auto">
      <b-form>
        <b-button :to="{ name: 'PrepareSessionPage' }" class="startButton px-2 mr-2">New Session</b-button>
      </b-form>
      <b-form>
        <b-button :to="{ name: 'JoinPage' }" class="joinButton px-2 mr-2">
          {{ $t("page.landing.meeting.join.buttons.start.label") }}</b-button
          >
      </b-form>
      <b-form class="px-2 mr-2">
        <a href="https://github.com/Sybit-Education/Diveni" target="_blank">
          <img :src="require('./images/GitHub-Mark-32px.png')" height="40px" width="40px"/>
        </a>
      </b-form>
      <locale-dropdown  />
    </b-navbar-nav>
  </b-navbar>
</template>

<script lang="ts">
import Vue from "vue";
import LocaleDropdown from "@/components/navigation/LocaleDropdown.vue";

export default Vue.extend({
  name: "TopNavigationBar",
  components: { LocaleDropdown },
  data() {
    return {
      userTheme: "light-Theme"
    }
  },
  created() {
    if (localStorage.getItem("user-theme")) {
      this.setTheme(localStorage.getItem("user-theme"));
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

<style>

/* TOP Navigation Bar */

.top-navigation {
  background-color: var(--topNavigationBarColor);
}
.top-navigation__nav-logo {
  height: 3rem;
}
.top-navigation__title {
  font-size: 2.5rem;
  font-weight: 700;
}

/* Dark Mode Switch */
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
