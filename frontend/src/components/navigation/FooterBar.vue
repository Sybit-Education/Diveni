<template>
  <footer class="py-5">
    <b-container>
      <div>
        &copy; 2022-{{ currentYear }} Diveni |
        <a href="https://docs.diveni.io">{{ $t("general.about.docs") }}</a> |
        <router-link to="/about">{{ $t("general.about.label") }}</router-link>
      </div>
      <div>Made with ❤️ by Diveni Development Team</div>
    </b-container>
  </footer>
</template>
<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "FooterBar",
  computed: {
    currentYear() {
      return new Date().getFullYear();
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
footer {
  min-height: 4rem;
  background-color: #22272e;
  color: #adbac7;
}
a {
  color: #539bf5;
  text-decoration: none;
}
</style>
