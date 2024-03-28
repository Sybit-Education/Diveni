module.exports = {
  pwa: {
    name: "Diveni",
    themeColor: "#8cc04d",
    msTileColor: "#000000",
    appleMobileWebAppCapable: "yes",
    appleMobileWebAppStatusBarStyle: "black",
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
