<template>
  <div>
    <b-dropdown :text="$t('general.about')" class="mt-4 mb-2">
      <b-dropdown-item @click="openFrontendLicenseTab()">{{
        $t("general.licenses.frontend")
      }}</b-dropdown-item>
      <b-dropdown-item @click="openBackendLicenseTab()">{{
        $t("general.licenses.backend")
      }}</b-dropdown-item>
    </b-dropdown>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "AboutDropdown",
  methods: {
    openFrontendLicenseTab() {
      window.open("/frontend-dependencies.json");
    },
    openBackendLicenseTab() {
      window.open("/backend-dependencies.txt");
    },
  },
});
</script>
