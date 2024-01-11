/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 AUME-Team 21/22, HTWG Konstanz
*/
import Vue, { createApp } from "@vue/compat";

import axios from "axios";
import VueAxios from "vue-axios";

import App from "./App.vue";
import "./registerServiceWorker";
import router from "./router";
import setupInterceptors from "./interceptors";
import { createPinia } from "pinia";

import { BootstrapVue, IconsPlugin, ModalPlugin } from "bootstrap-vue";

import "./assets/style/main.scss";

import VueFlicking from "@egjs/vue3-flicking";
import "@egjs/vue3-flicking/dist/flicking.css";

import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import i18n from "@/i18n";
import VueMeta from 'vue-meta'

Vue.use(IconsPlugin);
Vue.use(VueFlicking);
Vue.use(ModalPlugin);
Vue.use(BootstrapVue);
Vue.use(VueMeta)

setupInterceptors();

// Vue.config.productionTip = false;

const app = createApp(App);

const pinia = createPinia();

app
  .use(VueAxios, axios)
  .use(router)
  .use(BootstrapVue)
  .use(pinia)
  .use(i18n)
  .use(Toast, {})
  .mount("#app");
