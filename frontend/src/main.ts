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
import setupInterceptors from "./interceptors";
import { createPinia } from "pinia";
import { BootstrapVue, IconsPlugin, ModalPlugin } from "bootstrap-vue";
import "./assets/style/main.scss";
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import i18n from "@/i18n";
import vueDebounce from "vue-debounce";

Vue.use(IconsPlugin);
Vue.use(ModalPlugin);
Vue.use(BootstrapVue);

setupInterceptors();

// Vue.config.productionTip = false;

const app = createApp(App);

const pinia = createPinia();

app.directive("debounce", vueDebounce({ lock: true }));

app
  .use(VueAxios, axios)
  .use(router)
  .use(BootstrapVue)
  .use(pinia)
  .use(i18n)
  .use(Toast, {})
  .mount("#app");
