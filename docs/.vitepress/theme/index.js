import DefaultTheme from 'vitepress/theme'
import { onMounted } from 'vue'
import mediumZoom from 'medium-zoom'
import vitepressBackToTop from 'vitepress-plugin-back-to-top'
import 'vitepress-plugin-back-to-top/dist/style.css'

import './custom.css'

export default {
  ...DefaultTheme,
  setup() {
    onMounted(() => {
      mediumZoom('.main img', { background: 'var(--vp-c-bg)' });
    })
  },
  enhanceApp({ app }) {
    vitepressBackToTop({
      // default
      threshold:300
    })
  },
};
