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
      <div class="row" style="justify-content: center">
        <b-pagination class="customPagination"
                      first-class="customPagination"
                      v-model="currentPage"
                      :total-rows="rows"
                      :per-page="perPage"
                      @change="handlePageChange"
        >
        </b-pagination>
      </div>
      <div class="row" style="justify-content: center">
        <div v-if="loading" align="center" class="col-12 my-5">
          <b-spinner label="Loading..."></b-spinner>
        </div>
        <b-card-group deck v-for="card in paginatedData" :key=card.number class="my-3 col-md-4">
          <b-card class="pr-card"
                  align="center"
                  border-variant="secondary"
                  :header="`${card.updated_at}`"
                  footer-tag="footer"
                  header-border-variant="secondary">
            <b-card-text>{{ card.title }}</b-card-text>
            <template #footer>
              <b-link target="_blank" :href="card.html_url">#{{ card.number }}</b-link>
            </template>
          </b-card>
        </b-card-group>
      </div>

    </b-container>
  </div>
</template>
<script lang="ts">
import Vue from 'vue'
import apiService from "@/services/api.service";
import {PullRequestDto} from "@/types";
import Constants from "@/constants";
import dateUtil from "@/utils/dateUtil";


export default Vue.extend({
  name: "WhatsNewPage",
  data() {
    return {
      perPage: Constants.newsPageSize,
      currentPage: 1,
      apiPage: 1,
      items: [] as PullRequestDto[],
      loading: false,
    }
  },
  computed: {
    rows() {
      return this.items.length
    },
    totalPages() {
      return Math.ceil(this.items.length / this.perPage);
    },
    paginatedData() {
      const startIndex = (this.currentPage - 1) * this.perPage;
      const endIndex = startIndex + this.perPage;
      return this.items.slice(startIndex, endIndex);
    }
  },
  mounted() {
    this.fetchData(this.apiPage)

  },
  methods: {
    parseDate(data: string | any[]) {
      for (let i = 0; i < data.length; i++) {
        data[i].updated_at = dateUtil.convertDate(data[i].updated_at)
      }
    }
    ,
    async fetchData(page: number) {
      if (this.loading) {
        return;
      }
      this.loading = true;
      try {
        let data: PullRequestDto[] = await apiService.getPullRequests('closed', "updated", "desc", true, page, 100);
        data = data.filter(e => e.user_type !== Constants.botUserType)
        this.parseDate(data)
        this.items = this.items.concat(data)
      } catch (e) {
        console.error(`got error: ${e}`);
      } finally {
        this.loading = false;
      }
    },
    handlePageChange(nextPage) {
      if (nextPage === this.totalPages && !this.loading) {
        this.fetchData(++this.apiPage)
      } else {
        this.currentPage = nextPage;
      }
    }
  }
})


</script>


<style scoped>
.teaser {
  background: linear-gradient(var(--background-color-primary), var(--pictureGradientEnd)),
  url("~@/assets/img/diveni-background.png");
  background-size: cover;
  background-repeat: no-repeat;
}

.pr-card {
  background-color: var(--landingPageCardsBackground);
}

.jumbotron {
  background-color: rgba(255, 255, 255, 0.5);
}

.customPagination /deep/ li > button,
.customPagination /deep/ li > span {
  background-color: var(--landingPageCardsBackground) !important;
  color: var(--text-primary-color) !important;
  border-color: var(--text-primary-color) !important;;
}
</style>
