import Vue from 'vue';
import VueRouter, { RouteConfig } from 'vue-router';
import LandingPage from '../views/LandingPage.vue';
import JoinPage from '../views/JoinPage.vue';
import SessionPage from '../views/SessionPage.vue';
import MemberEstimatePage from '../views/MemberEstimatePage.vue';

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
  {
    path: '/',
    name: 'LandingPage',
    component: LandingPage,
  },
  {
    path: '/session',
    name: 'SessionPage',
    props: true,
    component: SessionPage,
  },
  {
    path: '/join',
    name: 'JoinPage',
    component: JoinPage,
  },
  {
    path: '/estimate',
    name: 'MemberEstimate',
    component: MemberEstimatePage,
  },
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

export default router;
