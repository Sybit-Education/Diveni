<template>
  <footer class="py-5">
    <b-container>
      <div>
        &copy; 2022 Diveni |
        <a href="https://docs.diveni.io">{{ $t("general.about.docs") }}</a> |
        <router-link to="/about">{{ $t("general.about.label") }}</router-link>
      </div>
      <div>Made with ❤️ by Diveni Development Team</div>
    </b-container>
  </footer>
</template>
<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "FooterBar",
});
</script>
<style scoped>
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
