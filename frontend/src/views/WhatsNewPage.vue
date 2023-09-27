<template>
  <div id="whats-new-page">
    <b-container fluid class="teaser my-5">
      <b-container>
        <b-jumbotron header="DIVENI" lead="What's new"/>
      </b-container>
    </b-container>

    <b-container>

        <div>
          <b-card-group deck>
            <b-card
              border-variant="secondary"
              header="Secondary"
              header-border-variant="secondary"
              align="center">
              <b-card-text>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</b-card-text>
            </b-card>

            <b-card
              border-variant="secondary"
              header="Secondary"
              header-border-variant="secondary"
              align="center">
              <b-card-text>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</b-card-text>
            </b-card>

            <b-card
              border-variant="secondary"
              header="Secondary"
              header-border-variant="secondary"
              align="center">
              <b-card-text>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</b-card-text>
            </b-card>


          </b-card-group>
        </div>
        <div class="mt-3">
          <b-card-group deck>

            <b-card
              border-variant="secondary"
              header="Secondary"
              header-border-variant="secondary"
              align="center">
              <b-card-text>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</b-card-text>
            </b-card>
            <b-card
              border-variant="secondary"
              header="Secondary"
              header-border-variant="secondary"
              align="center">
              <b-card-text>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</b-card-text>
            </b-card>
            <b-card
              border-variant="secondary"
              header="Secondary"
              header-border-variant="secondary"
              align="center">
              <b-card-text>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</b-card-text>
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
