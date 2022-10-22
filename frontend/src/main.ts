/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 AUME-Team 21/22, HTWG Konstanz
*/
import { createApp } from "vue";

import axios from "axios";
import VueAxios from "vue-axios";

import App from "./App.vue";
import "./registerServiceWorker";
import router from "./router";
import store from "./store";
import { i18n } from "./i18n";
import setupInterceptors from "./interceptors";

import { BootstrapVue, IconsPlugin } from "bootstrap-vue";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";

import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";

setupInterceptors();

createApp(App)
  .use(VueAxios, axios)
  .use(IconsPlugin)
  .use(BootstrapVue)
  .use(Toast, {})
  .use(router)
  .use(store)
  .use(i18n)
  .mount('#app');
