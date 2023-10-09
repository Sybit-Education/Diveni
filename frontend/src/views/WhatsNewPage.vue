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
        <b-card-group deck v-for="card in items" :key=card.number class="my-3 col-md-4">
          <b-card
            align="center"
            border-variant="secondary"
            :header="`${card.updated_at}`"
            footer-tag="footer"
            header-border-variant="secondary">
            <b-card-text>{{ card.title }}</b-card-text>
            <template #footer>
              <b-link target="_blank" :href="card.html_url">#{{card.number}}</b-link>
            </template>
          </b-card>
        </b-card-group>
      </div>
      <div v-if="loading" align="center">
        <b-spinner label="Loading..."></b-spinner>
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
      currentSet: 1,
      items: [] as PullRequestDto[],
      loading:false
    }
  },
  mounted() {
    this.fetchData(1)
    window.addEventListener('scroll', this.onScroll);

  },
  beforeDestroy() {
    // Remove the scroll event listener when the component is destroyed
    window.removeEventListener('scroll', this.onScroll);
  },
  methods: {
    onScroll(){
      if(window.scrollY !== 0){
        const bottomOffset = window.innerHeight + window.scrollY;
        const documentHeight = document.documentElement.offsetHeight;
        const isAtBottom = bottomOffset >= documentHeight;

        if(isAtBottom ){
          this.fetchData(this.currentSet)
        }
      }
    },
    parseDate(data){
      for (let i = 0; i < data.length; i++) {
        const pr = data[i];
        pr.updated_at = dateUtil.convertDate(pr.updated_at)
      }
    }
    ,
    async fetchData(set) {
      if(this.loading){
        return;
      }
      this.loading = true;
      try {
        let localSet = set;
        let data: PullRequestDto[] = await apiService.getPullRequests('closed', localSet, Constants.newsPageSize);
        let dataCount = 0;
        while (dataCount < 80 && data.length != 0) {
          data = data.filter(e => e.merged_at != null && e.user_type !== Constants.botUserType)
          dataCount+=data.length;
          this.parseDate(data)
          this.items = this.items.concat(data);
          localSet++
          data = await apiService.getPullRequests('closed', localSet, Constants.newsPageSize);
        }
        this.currentSet = localSet;
      } catch (e) {
        console.error(`got error: ${e}`);
      }finally {
        this.loading = false;
      }
    }
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
