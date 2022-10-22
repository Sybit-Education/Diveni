<template>
  <b-container id="about-page">
    <h1>{{ $t("general.about.label") }} Diveni</h1>
    <p>
      WebApp to do Planning Poker with remote teams using external issue tracker
      as source of stories.
    </p>

    <h2>Open Source & Contribution</h2>
    <p>
      Diveni is open source and published under
      <a
        href="https://github.com/Sybit-Education/Diveni/blob/main/LICENSE"
        target="_blank"
        >GNU Affero General Public License v3.0</a
      >.
    </p>
    <p>
      Contributions are welcome on our public repository at
      <a href="https://github.com/Sybit-Education/Diveni" target="_blank"
        >GitHub</a
      >:
    </p>
    <ul>
      <li>
        <a href="https://github.com/Sybit-Education/Diveni/" target="_blank"
          >Code</a
        >
      </li>
      <li>
        <a
          href="https://github.com/Sybit-Education/Diveni/issues"
          target="_blank"
          >Issues</a
        >
      </li>
      <li>
        <a
          href="https://github.com/Sybit-Education/Diveni/discussions"
          target="_blank"
          >Discussions</a
        >
      </li>
    </ul>

    <h2>Credits</h2>
    <h3>HTWG Konstanz</h3>
    <p>TBD</p>

    <h3>Sybit GmbH</h3>
    <p>
      Sybit supports teaching at the university at HTWG Konstanz and acted as an
      industry partner in this project. A product owner represented Sybit's
      requirements to the students and released the sprints in the agile project
      development.
    </p>
    <p>
      Sybit GmbH will continue to support and maintain the project as an open
      source project.
    </p>
    <h2>License</h2>
    <p>
      Diveni is licensed under
      <a
        href="https://github.com/Sybit-Education/Diveni/blob/main/LICENSE"
        target="_blank"
        >AGPL-3.0 license</a
      >.
    </p>
    <h2>Used Libraries & Licenses</h2>
    <ul>
      <li>
        <a href="/frontend-dependencies.json" target="_blank">
          {{ $t("general.licenses.frontend") }}
        </a>
      </li>
      <li>
        <a href="/backend-dependencies.txt" target="_blank">
          {{ $t("general.licenses.backend") }}
        </a>
      </li>
      <li>
        <a href="https://crowdin.com/project/diveni" target="_blank">
          {{ $t("general.licenses.translations") }}
        </a>
      </li>
    </ul>
  </b-container>
</template>
<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "AboutPage",
});
</script>
