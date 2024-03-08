import { createRouter, createWebHistory } from "vue-router";
import LandingPage from "../views/LandingPage.vue";

const routes = [
  {
    path: "/",
    name: "LandingPage",
    component: LandingPage,
  },
  {
    path: "/session",
    name: "SessionPage",
    props: true,
    component: () => import(/* webpackChunkName: "session" */ "../views/SessionPage.vue"),
  },
  {
    path: "/result",
    name: "ResultPage",
    props: true,
    component: () => import(/* webpackChunkName: "result" */ "../views/ResultPage.vue"),
  },
  {
    path: "/join",
    name: "JoinPage",
    component: () => import(/* webpackChunkName: "join" */ "../views/JoinPage.vue"),
  },
  {
    path: "/vote",
    name: "MemberVotePage",
    props: true,
    component: () => import(/* webpackChunkName: "vote" */ "../views/MemberVotePage.vue"),
  },
  {
    path: "/prepare",
    name: "PrepareSessionPage",
    component: () => import(/* webpackChunkName: "prepare" */ "../views/PrepareSessionPage.vue"),
  },
  {
    path: "/jiraCallback",
    name: "JiraCallbackPage",
    component: () => import(/* webpackChunkName: "jira" */ "../views/JiraCallbackPage.vue"),
  },
  {
    path: "/about",
    name: "AboutPage",
    component: () => import(/* webpackChunkName: "about" */ "../views/AboutPage.vue"),
  },
  {
    path: "/whats-new",
    name: "WhatsNewPage",
    component: () => import(/* webpackChunkName: "whats-new" */ "../views/WhatsNewPage.vue"),
  },
  {
    path: "/:catchAll(.*)",
    component: LandingPage,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: routes,
});

export default router;
