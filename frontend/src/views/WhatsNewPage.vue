<template>
  <div id="whats-new-page">
    <b-container fluid class="teaser my-5">
      <b-container>
        <b-jumbotron header="DIVENI">
          <template #lead>
            {{ $t("page.landing.news.buttons.info.label") }}
          </template>
        </b-jumbotron>
      </b-container>
    </b-container>

    <b-container class="pb-5">
      <div>
        <b-pagination align="center"
                      v-model="currentPage"
                      :total-rows="rows"
                      :per-page="perPage"
                      aria-controls="my-table"
        ></b-pagination>
      </div>
      <div>
        <b-card-group deck>
          <b-card
            border-variant="secondary"
            header="Date"
            header-border-variant="secondary"
            align="center">
            <b-card-text>Title...</b-card-text>
            <b-button variant="primary">LINK</b-button>
          </b-card>

          <b-card
            border-variant="secondary"
            header="Date"
            header-border-variant="secondary"
            align="center">
            <b-card-text>Title...</b-card-text>
            <b-button variant="primary">LINK</b-button>
          </b-card>

          <b-card
            border-variant="secondary"
            header="Date"
            header-border-variant="secondary"
            align="center">
            <b-card-text>Title...</b-card-text>
            <b-button variant="primary">LINK</b-button>
          </b-card>


        </b-card-group>
      </div>
      <div class="mt-3">
        <b-card-group deck>

          <b-card
            border-variant="secondary"
            header="Date"
            header-border-variant="secondary"
            align="center">
            <b-card-text>Title...</b-card-text>
            <b-button variant="primary">LINK</b-button>
          </b-card>
          <b-card
            border-variant="secondary"
            header="Date"
            header-border-variant="secondary"
            align="center">
            <b-card-text>Title...</b-card-text>
            <b-button variant="primary">LINK</b-button>
          </b-card>
          <b-card
            border-variant="secondary"
            header="Date"
            header-border-variant="secondary"
            align="center">
            <b-card-text>Title...</b-card-text>
            <b-button variant="primary">LINK</b-button>
          </b-card>

        </b-card-group>
      </div>
    </b-container>
  </div>
</template>


<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  name: "WhatsNewPage"
})
</script>


<style scoped>
.teaser {
  background: linear-gradient(rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.5)),
  url("~@/assets/img/diveni-background.png");
  background-size: cover;
  background-repeat: no-repeat;
}

.jumbotron {
  background-color: rgba(255, 255, 255, 0.5);
}
</style>
