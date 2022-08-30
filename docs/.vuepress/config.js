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
    editLinks: false,
    lastUpdated: false,
    nav: [
      {
        text: 'Guide',
        link: '/guide/',
      },
      {
        text: 'App',
        link: 'https://diveni.io'
      },
      {
        text: 'License',
        link: '/license'
      }
    ],
    sidebar: {
      '/guide/': [
        {
          collapsible: true,
          children: [
            '',
            'user',
            'install',
            'develop',
            'architecture',
            'testing'
          ]
        }
      ],
    }
  },
  plugins: [
    '@vuepress/plugin-back-to-top',
    '@vuepress/plugin-medium-zoom',
    'vuepress-plugin-mermaidjs',
  ]
}
