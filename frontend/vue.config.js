module.exports = {
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
  chainWebpack: (config) => {
    config.resolve.alias.set('vue', '@vue/compat')

    config.module
      .rule('vue')
      .use('vue-loader')
      .tap((options) => {
        return {
          ...options,
          compilerOptions: {
            compatConfig: {
              MODE: 2
            }
          }
        }
      })
  }
};
