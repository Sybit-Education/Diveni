/* eslint-disable @typescript-eslint/ban-types, @typescript-eslint/no-explicit-any */
declare module "*.vue" {
  import type { DefineComponent } from "vue";
  const component: DefineComponent<Record<string, unknown>, Record<string, unknown>, any>;
  export default component;
}

declare module "vue3-steppy";
