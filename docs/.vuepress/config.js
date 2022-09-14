module.exports = {
  lang: 'en-US',
  title: 'Diveni',
  description: 'The Planning-Poker App',
  base: '/Diveni/',
  head: [
    ['meta', { name: 'theme-color', content: '#3eaf7c' }],
    ['meta', { name: 'apple-mobile-web-app-capable', content: 'yes' }],
    ['meta', { name: 'apple-mobile-web-app-status-bar-style', content: 'black' }]
  ],
  themeConfig: {
    repo: 'Sybit-Education/Diveni',
    docsDir: 'docs',
    docsBranch: 'main',
    editLinks: true,
    editLinkText: 'Help us improve this page!',
    lastUpdated: 'Last Updated',
    sidebarDepth: 2,
    smoothScroll: true,
    nav: [
      {
        text: 'Guides',
        link: '/guide/',
        items: [
          { text: "Users Guide", link: '/guide/user/' },
          { text: "Installation Guide", link: '/guide/install/' },
          { text: "Contribution Guide", link: "/guide/contribution/"},
          { text: "Architecture", link: "/guide/architecture/"},
          { text: "Testing", link: "/guide/testing/"}
        ]
      },
      {
        text: 'App',
        link: 'https://diveni.io'
      },
      {
        text: 'Sponsors',
        link: '/sponsors'
      },
      {
        text: 'License',
        link: '/license'
      }
    ],
    sidebar: {
      '/guide/': [
        'user',
        'install',
        'contribution',
        'architecture',
        'testing'
      ]
    }
  },
  plugins: [
    '@vuepress/plugin-back-to-top',
    '@vuepress/plugin-medium-zoom',
    'vuepress-plugin-mermaidjs',
  ]
}
