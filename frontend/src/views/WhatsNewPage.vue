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
                      :total-rows="totalRows"
                      :per-page=perPage
        ></b-pagination>
      </div>
      <div class="row">
        <div v-for="card in currentElements" :key=card.id class="my-2 col-md-4">
          <b-card-group deck>
            <b-card
              border-variant="secondary"
              header="Date"
              header-border-variant="secondary"
              align="center">
              <b-card-text>{{ card.title }}</b-card-text>
              <b-button variant="primary">{{ card.link }}</b-button>
            </b-card>
          </b-card-group>
        </div>
      </div>
    </b-container>
  </div>
</template>
<script lang="ts">
import Vue from 'vue'


export default Vue.extend({
  name: "WhatsNewPage",
  data() {
    return {

      perPage: 6,
      currentPage: 1,
      items: [
        {id: 1, title: 'Fred', link: 'Flintstone'},
        {id: 2, title: 'Wilma', link: 'Flintstone'},
        {id: 3, title: 'Barney', link: 'Rubble'},
        {id: 4, title: 'Betty', link: 'Rubble'},
        {id: 5, title: 'Pebbles', link: 'Flintstone'},
        {id: 6, title: 'Bamm Bamm', link: 'Rubble'},
        {id: 7, title: 'The Great', link: 'Gazzoo'},
        {id: 8, title: 'Rockhead', link: 'Slate'},
        {id: 9, title: 'Pearl', link: 'Slaghoople'},
        {id: 10, title: 'Petko', link: 'Slaghoople'},
        {id: 11, title: 'Ivan', link: 'Slaghoople'},
        {id: 12, title: 'Gosho', link: 'Slaghoople'},
        {id: 13, title: 'C', link: 'Slaghoople'},
      ]
    }
  },
  computed: {
    totalRows() {
      return this.items.length;
    },
    currentElements() {
      let end = this.currentPage * this.perPage;
      let start = end - this.perPage;
      return this.items.slice(start, end);
    },

  }
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
