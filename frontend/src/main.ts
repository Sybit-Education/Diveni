/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 AUME-Team 21/22, HTWG Konstanz
*/
import Vue, { createApp } from "vue";

import axios from "axios";
import VueAxios from "vue-axios";

import App from "./App.vue";
import "./registerServiceWorker";
import router from "./router";
import store from "./store";
import { i18n } from "./i18n";
import setupInterceptors from "./interceptors";

import { BootstrapVue, IconsPlugin } from "bootstrap-vue";
// Import Bootstrap and BootstrapVue CSS files (order is important)
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";

import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";

setupInterceptors();

const app = createApp({
  router,
  store,
  i18n,
  ...App,
});

app.use(VueAxios, axios);
app.use(Toast, {});
app.use(BootstrapVue);

app.mount("#app");
