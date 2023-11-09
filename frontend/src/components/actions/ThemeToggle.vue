<template>
  <div class="flex">
    <div class="mode-toggle" @click="modeToggle">
      <div class="toggle">
        <div id="dark-mode" type="checkbox"></div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
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
    modeToggle() {
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
        this.dark();
      } else {
        this.light();
      }
      window.dispatchEvent(
        new CustomEvent("user-theme-localstorage-changed", {
          detail: {
            storage: localStorage.getItem("user-theme"),
          },
        })
      );
    },
    dark() {
      document.querySelector("body")?.classList.add("dark-mode");
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
    light() {
      document.querySelector("body")?.classList.remove("dark-mode");
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

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
@import "@/assets/style/variables.scss";

.mode-toggle {
  position: relative;
  padding: 0;
  width: 44px;
  height: 24px;
  min-width: 36px;
  min-height: 20px;
  background-color: var(--topNavigationBarColor);
  border: 1px solid;
  border-radius: 24px;
  border-color: var(--text-primary-color);
  outline: 0;
  overflow: hidden;
  cursor: pointer;
  z-index: 2;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  -webkit-touch-callout: none;
  appearance: none;
  transition: background-color 0.1s ease;

  .toggle {
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    margin: auto;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: 3px solid transparent;
    box-shadow: inset 0 0 0 2px var(--text-primary-color);
    overflow: hidden;
    transition: transform 0.1s ease;

    #dark-mode {
      position: relative;
      width: 100%;
      height: 100%;
      overflow: hidden;
      border-radius: 50%;

      &:before {
        content: "";
        position: relative;
        width: 100%;
        height: 100%;
        left: 50%;
        float: left;
        background-color: var(--text-primary-color);
        transition: border-radius 0.1s ease, width 0.1s ease, height 0.1s ease, left 0.1s ease,
          transform 0.1s ease;
      }
    }
  }
}

body.dark-mode {
  .mode-toggle {
    background-color: lighten(#262626, 5%);
    border-color: var(--text-primary-color);

    .toggle {
      transform: translateX(21px);
      box-shadow: inset 0 0 0 2px var(--text-primary-color);

      #dark-mode {
        &:before {
          border-radius: 50%;
          width: 150%;
          height: 85%;
          left: 45%;
          transform: translate(-10%, -40%), rotate(-35deg);
          background-color: white;
        }
      }
    }
  }
}
</style>
