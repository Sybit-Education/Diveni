import Vue from 'vue';
import axios, { AxiosStatic } from 'axios';
import App from './App.vue';
import './registerServiceWorker';
import router from './router';
import store from './store';
import vuetify from './plugins/vuetify';

axios.defaults.baseURL = 'http://192.168.1.225:8088';
Vue.prototype.$axios = axios;
declare module 'vue/types/vue' {
  interface VueAxios {
    $axios: AxiosStatic;
  }
}

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App),
}).$mount('#app');
