import Vue from 'vue';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';
import axios from 'axios';
import VueAxios from 'vue-axios';
import VueFlicking from '@egjs/vue-flicking';
import App from './App.vue';
import './registerServiceWorker';
import router from './router';
import store from './store';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import '@egjs/vue-flicking/dist/flicking.css';

Vue.use(VueAxios, axios);
Vue.use(IconsPlugin);
Vue.use(BootstrapVue);
Vue.use(VueFlicking);

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app');
