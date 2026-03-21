/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 AUME-Team 21/22, HTWG Konstanz
*/
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import setupInterceptors from "./interceptors";
import { createPinia } from "pinia";
import {
  createBootstrap,
  BAlert,
  BBadge,
  BButton,
  BCard,
  BCardBody,
  BCardFooter,
  BCardGroup,
  BCardHeader,
  BCardImg,
  BCardSubtitle,
  BCardText,
  BCardTitle,
  BCol,
  BCollapse,
  BContainer,
  BDropdown,
  BDropdownDivider,
  BDropdownItem,
  BForm,
  BFormCheckbox,
  BFormGroup,
  BFormInput,
  BFormSelect,
  BFormTextarea,
  BImg,
  BInputGroup,
  BInputGroupText,
  BLink,
  BListGroup,
  BListGroupItem,
  BModal,
  BNav,
  BNavItem,
  BNavItemDropdown,
  BNavbar,
  BNavbarBrand,
  BNavbarNav,
  BNavbarToggle,
  BOverlay,
  BPagination,
  BPopover,
  BRow,
  BSpinner,
  BTab,
  BTabs,
  vBModal,
} from "bootstrap-vue-next";
import "./assets/style/main.scss";
import "bootstrap-vue-next/dist/bootstrap-vue-next.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import i18n from "@/i18n";
import vueDebounce from "vue-debounce";

setupInterceptors();

const app = createApp(App);

const pinia = createPinia();

app.directive("debounce", vueDebounce({ lock: true }));
app.directive("b-modal", vBModal);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const components: Record<string, any> = {
  BAlert,
  BBadge,
  BButton,
  BCard,
  BCardBody,
  BCardFooter,
  BCardGroup,
  BCardHeader,
  BCardImg,
  BCardSubtitle,
  BCardText,
  BCardTitle,
  BCol,
  BCollapse,
  BContainer,
  BDropdown,
  BDropdownDivider,
  BDropdownItem,
  BForm,
  BFormCheckbox,
  BFormGroup,
  BFormInput,
  BFormSelect,
  BFormTextarea,
  BImg,
  BInputGroup,
  BInputGroupText,
  BLink,
  BListGroup,
  BListGroupItem,
  BModal,
  BNav,
  BNavItem,
  BNavItemDropdown,
  BNavbar,
  BNavbarBrand,
  BNavbarNav,
  BNavbarToggle,
  BOverlay,
  BPagination,
  BPopover,
  BRow,
  BSpinner,
  BTab,
  BTabs,
};

Object.entries(components).forEach(([name, component]) => {
  app.component(name, component);
});

app.use(createBootstrap()).use(router).use(pinia).use(i18n).use(Toast, {}).mount("#app");
