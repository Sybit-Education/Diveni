/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 AUME-Team 21/22, HTWG Konstanz
*/
import Vue from "vue";

import axios from "axios";
import VueAxios from "vue-axios";

import App from "./App.vue";
import "./registerServiceWorker";
import router from "./router";
import store from "./store";
import i18n from "./i18n";
import setupInterceptors from "./interceptors";

import { BootstrapVue, IconsPlugin } from "bootstrap-vue";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";

import VueFlicking from "@egjs/vue-flicking";

import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";

import VueMarkdownEditor from "@kangc/v-md-editor";
import "@kangc/v-md-editor/lib/style/base-editor.css";

Vue.use(VueAxios, axios);
Vue.use(IconsPlugin);
Vue.use(BootstrapVue);
Vue.use(VueFlicking);
Vue.use(Toast, {});
Vue.use(VueMarkdownEditor);

setupInterceptors();

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  i18n,
  render: (h) => h(App),
}).$mount("#app");
