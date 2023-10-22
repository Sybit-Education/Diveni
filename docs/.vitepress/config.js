import { defineConfig } from 'vitepress'
import { withMermaid } from 'vitepress-plugin-mermaid'
import { SearchPlugin } from "vitepress-plugin-search"

export default withMermaid(
  defineConfig({
    lang: 'en-US',
    title: "Diveni",
    description: "The Planning-Poker App",
    base: '/Diveni/',
    cleanUrls: true,
    lastUpdated: true,
    ignoreDeadLinks: "localhostLinks",
    head: [
      ['meta', { name: 'theme-color', content: '#3eaf7c' }],
      ['meta', { name: 'apple-mobile-web-app-capable', content: 'yes' }],
      ['meta', { name: 'apple-mobile-web-app-status-bar-style', content: 'black' }]
    ],
    themeConfig: {
      logo: './logo.svg',
      nav: [
        {
          text: 'Guides',
          link: '/guide',
          items: [
            { text: "Users Guide", link: '/guide/user' },
            { text: "Installation Guide", link: '/guide/install' },
            { text: "Contribution Guide", link: "/guide/contribution"},
            { text: "Translation Guide", link: "/guide/translations"},
            { text: "Architecture", link: "/guide/architecture"},
            { text: "Testing", link: "/guide/testing"}
          ]
        },
        { text: 'App', link: 'https://diveni.io' },
        { text: 'Code of Conduct', link: '/code_of_conduct' },
        { text: 'Sponsors', link: '/sponsors' },
        { text: 'License', link: '/license' }
      ],

      sidebar: [
        {
          items: [
            { text: "Users Guide", link: '/guide/user' },
            { text: "Installation Guide", link: '/guide/install' },
            { text: "Contribution Guide", link: "/guide/contribution"},
            { text: "Translation Guide", link: "/guide/translations"},
            { text: "Architecture", link: "/guide/architecture"},
            { text: "Testing", link: "/guide/testing"}
          ]
        }
      ],

      socialLinks: [
        { icon: 'github', link: 'https://github.com/Sybit-Education/Diveni' },
        { icon: 'discord', link: 'https://discord.com/channels/935641426216222730/' }
      ],

      footer: {
        message: 'Made by Diveni Development Team with ❤️ at Lake Constance'
      },

      editLink: {
        pattern: 'https://github.com/Sybit-Education/Diveni/edit/main/docs/:path',
        text: 'Help us improve this page!'
      }
    },
    vite: {
      plugins: [
        SearchPlugin()
      ]
    }
  })
);
