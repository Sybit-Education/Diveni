module.exports = {
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
  configureWebpack: {
    optimization: {
      splitChunks: {
        cacheGroups: {
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: "vendor",
            chunks: "all",
            enforce: true,
            priority: 1,
            maxInitialRequests: 3,
            maxAsyncRequests: 5, // hier wird die Anzahl der gleichzeitigen Anfragen für nachfolgende Chunk-Dateien begrenzt
          },
        },
      },
    },
  },
  chainWebpack: (config) => {
    config.resolve.alias.set("vue", "@vue/compat");
    config.module
      .rule("vue")
      .use("vue-loader")
      .tap((options) => {
        return {
          ...options,
          compilerOptions: {
            compatConfig: {
              MODE: 2,
            },
          },
        };
      });
  },
};
