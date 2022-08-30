<template>
  <b-dropdown :text="$t('general.about.label')" class="mt-4 mb-2">
    <b-dropdown-item href="https://github.com/Sybit-Education/Diveni">
      {{ $t("general.about.repository") }}
    </b-dropdown-item>
    <b-dropdown-item href="https://docs.diveni.io">
      {{ $t("general.about.documentation") }}
    </b-dropdown-item>
    <b-dropdown-divider></b-dropdown-divider>
    <b-dropdown-item @click="openFrontendLicenseTab()">
      {{ $t("general.licenses.frontend") }}
    </b-dropdown-item>
    <b-dropdown-item @click="openBackendLicenseTab()">
      {{ $t("general.licenses.backend") }}
    </b-dropdown-item>
  </b-dropdown>
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
