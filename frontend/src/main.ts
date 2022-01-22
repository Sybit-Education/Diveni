import Vue from "vue";
import { BootstrapVue, IconsPlugin } from "bootstrap-vue";
import axios from "axios";
import VueAxios from "vue-axios";
import VueFlicking from "@egjs/vue-flicking";
import App from "./App.vue";
import "./registerServiceWorker";
import router from "./router";
import store from "./store";
import i18n from "./i18n";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import "@egjs/vue-flicking/dist/flicking.css";
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import setupInterceptors from "./interceptors";

Vue.use(VueAxios, axios);
Vue.use(IconsPlugin);
Vue.use(BootstrapVue);
Vue.use(VueFlicking);
Vue.use(Toast, {});

setupInterceptors();

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  i18n,
  render: (h) => h(App),
}).$mount("#app");
