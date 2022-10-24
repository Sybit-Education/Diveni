import Vue from "vue";
import { createWebHistory, createRouter } from "vue-router";
import { RouteRecordRaw } from "vue-router";
import LandingPage from "../views/LandingPage.vue";
import JoinPage from "../views/JoinPage.vue";
import SessionPage from "../views/SessionPage.vue";
import MemberVotePage from "../views/MemberVotePage.vue";
import PrepareSessionPage from "../views/PrepareSessionPage.vue";
import ResultPage from "../views/ResultPage.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "LandingPage",
    component: LandingPage,
  },
  {
    path: "/session",
    name: "SessionPage",
    props: true,
    component: SessionPage,
  },
  {
    path: "/result",
    name: "ResultPage",
    props: true,
    component: ResultPage,
  },
  {
    path: "/join",
    name: "JoinPage",
    component: JoinPage,
  },
  {
    path: "/vote",
    name: "MemberVotePage",
    props: true,
    component: MemberVotePage,
  },
  {
    path: "/prepare",
    name: "PrepareSessionPage",
    component: PrepareSessionPage,
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
    path: "*",
    component: LandingPage,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
