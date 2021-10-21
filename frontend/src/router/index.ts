import Vue from 'vue';
import VueRouter, { RouteConfig } from 'vue-router';
import LandingPage from '../views/LandingPage.vue';
import JoinPage from '../views/JoinPage.vue';

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
  {
    path: '/',
    name: 'LandingPage',
    component: LandingPage,
  },
  {
    path: '/join',
    name: 'JoinPage',
    component: JoinPage,
  },
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

export default router;
