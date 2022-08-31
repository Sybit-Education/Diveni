<template>
  <b-nav-dropdown :text="$t('general.about')">
    <b-dropdown-item @click="openFrontendLicenseTab()">
      {{ $t("general.licenses.frontend")}}
    </b-dropdown-item>
    <b-dropdown-item @click="openBackendLicenseTab()">
      {{$t("general.licenses.backend")}}
    </b-dropdown-item>
  </b-nav-dropdown>
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
