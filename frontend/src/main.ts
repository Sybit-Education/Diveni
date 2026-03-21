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
import { createBootstrap } from "bootstrap-vue-next/plugins/createBootstrap";
import * as BvnComponents from "bootstrap-vue-next/components";
import { vBModal } from "bootstrap-vue-next/directives/BModal";
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

Object.entries(BvnComponents).forEach(([name, component]) => {
  app.component(name, component);
});

app.use(createBootstrap()).use(router).use(pinia).use(i18n).use(Toast, {}).mount("#app");
