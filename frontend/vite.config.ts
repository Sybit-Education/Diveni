import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { VitePWA } from "vite-plugin-pwa";
import { fileURLToPath, URL } from "node:url";

export default defineConfig(({ command }) => ({
  plugins: [
    vue(),
    VitePWA({
      registerType: "autoUpdate",
      workbox: {
        globPatterns: ["**/*.{js,css,html,ico,png,svg,json,woff2}"],
        navigateFallbackDenylist: [/^\/index\.html$/],
        maximumFileSizeToCacheInBytes: 4 * 1024 * 1024,
      },
      manifest: {
        name: "Diveni",
        short_name: "Diveni",
        theme_color: "#8cc04d",
        background_color: "#000000",
        display: "standalone",
        icons: [
          {
            src: "./img/icons/logo192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "./img/icons/logo512.png",
            sizes: "512x512",
            type: "image/png",
          },
          {
            src: "./img/icons/logo192.png",
            sizes: "192x192",
            type: "image/png",
            purpose: "maskable",
          },
          {
            src: "./img/icons/logo512.png",
            sizes: "512x512",
            type: "image/png",
            purpose: "maskable",
          },
        ],
      },
    }),
  ],
  define: {
    global: "globalThis",
  },
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    port: 8080,
  },
  build: {
    outDir: "dist",
  },
  css: {
    preprocessorOptions: {
      scss: {
        silenceDeprecations: [
          "import",
          "legacy-js-api",
          "if-function",
          "global-builtin",
          "color-functions",
          "abs-percent",
        ],
      },
    },
  },
  esbuild: command === "build" ? { drop: ["console"] } : undefined,
}));
