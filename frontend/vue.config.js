const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: "http://localhost:8080",
  },
  pwa: {
    manifestOptions: {
      icons: [],
    },
    iconPaths: {
      favicon32: "./favicon.ico",
      favicon16: "./favicon.ico",
      appleTouchIcon: "./icons/apple-touch-icon.png",
      maskIcon: "./favicon.ico",
      msTileImage: "./favicon.ico",
    },
  },
});
