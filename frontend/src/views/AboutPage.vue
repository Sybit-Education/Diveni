<template>
  <b-container id="about-page">
    <h1>{{ $t("general.about.label") }} Diveni</h1>
    <p>
      WebApp to do Planning Poker with remote teams using external issue tracker as source of
      stories.
    </p>

    <h2 style="color: var(--text-primary-color);">Open Source & Contribution</h2>
    <p>
      Diveni is open source and published under
      <a href="https://github.com/Sybit-Education/Diveni/blob/main/LICENSE" target="_blank" style="color: var(--linkColor);"
        >GNU Affero General Public License v3.0</a
      >.
    </p>
    <p>
      Contributions are welcome on our public repository at
      <a href="https://github.com/Sybit-Education/Diveni" target="_blank" style="color: var(--linkColor);">GitHub</a>:
    </p>
    <ul>
      <li><a href="https://github.com/Sybit-Education/Diveni/" target="_blank" style="color: var(--linkColor);">Code</a></li>
      <li><a href="https://github.com/Sybit-Education/Diveni/issues" target="_blank" style="color: var(--linkColor);">Issues</a></li>
      <li>
        <a href="https://github.com/Sybit-Education/Diveni/discussions" target="_blank" style="color: var(--linkColor);"
          >Discussions</a
        >
      </li>
    </ul>

    <h2 style="color: var(--text-primary-color);">Credits</h2>
    <h3 style="color: var(--text-primary-color);">HTWG Konstanz</h3>
    <p>TBD</p>

    <h3 style="color: var(--text-primary-color);">Sybit GmbH</h3>
    <p>
      Sybit supports teaching at the university at HTWG Konstanz and acted as an industry partner in
      this project. A product owner represented Sybit's requirements to the students and released
      the sprints in the agile project development.
    </p>
    <p>Sybit GmbH will continue to support and maintain the project as an open source project.</p>
    <h2 style="color: var(--text-primary-color);">License</h2>
    <p>
      Diveni is licensed under
      <a href="https://github.com/Sybit-Education/Diveni/blob/main/LICENSE" target="_blank" style="color: var(--linkColor);"
        >AGPL-3.0 license</a
      >.
    </p>
    <h2 style="color: var(--text-primary-color);">Used Libraries & Licenses</h2>
    <ul>
      <li>
        <a href="/frontend-dependencies.json" target="_blank" style="color: var(--linkColor);">
          {{ $t("general.licenses.frontend") }}
        </a>
      </li>
      <li>
        <a href="/backend-dependencies.txt" target="_blank" style="color: var(--linkColor);">
          {{ $t("general.licenses.backend") }}
        </a>
      </li>
      <li>
        <a href="https://crowdin.com/project/diveni" target="_blank" style="color: var(--linkColor);">
          {{ $t("general.licenses.translations") }}
        </a>
      </li>
    </ul>
  </b-container>
</template>
<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "AboutPage",
});
</script>
