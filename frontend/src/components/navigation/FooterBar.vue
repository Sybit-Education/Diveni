<template>
  <footer>
    <b-container>
      &copy; 2022 Diveni |
      <a href="https://github.com/Sybit-Education/Diveni/blob/main/LICENSE" target="_blank"
        >AGPL-3.0 license</a
      >
      <about-dropdown />
    </b-container>
  </footer>
</template>
<style scoped>
footer {
  min-height: 3rem;
  background-color: #22272e;
  color: #adbac7;
}
a {
  color: #539bf5;
  text-decoration: none;
}
</style>
<script>
import AboutDropdown from "@/components/navigation/AboutDropdown";
export default {
  components: { AboutDropdown },
};
</script>
