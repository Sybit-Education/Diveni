<template>
  <div class="row">
    <div class="wrapper" :style="{transform: `translateX(-${updateValue  * 2.5/ items.length}%)` }">
      <div class="set" v-for="(item, index) in allItems" :key="index">
        <b-card class="connectorCards" :class="getClass(item)">
          <b-card-title>
            <div>
              <b-img
                :src="require(`@/assets/${getClass(item).toLowerCase()}.png`)"
                :class="getClass(item) + 'Pic'"
              />
            </div>
            <div class="title">
              {{item.title}}
            </div>
          </b-card-title>
          <b-card-text>
            {{ item.description }}
          </b-card-text>
        </b-card>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "CarouselComponent",
    data() {
      return {
        updateValue: 0,
        items: [
          {title : "Jira Server", description: this.$t("page.landing.meeting.connectors.jiraServer.description")},
          {title : "Jira Cloud", description: this.$t("page.landing.meeting.connectors.jiraCloud.description")},
          {title: "Azure DevOps", description: this.$t("page.landing.meeting.connectors.azureDevOps.description")},
          {title : "Github", description: this.$t("page.landing.meeting.connectors.github.description")},
          {title : "Gitlab", description: this.$t("page.landing.meeting.connectors.gitlab.description")},
        ],
      }
    },
  computed: {
    allItems() {
      return [...this.items, ...this.items];
    }
  },
  mounted() {
    setInterval(() => {
      this.updateValue = (this.updateValue + 0.04) % (this.allItems.length * 17.85);

    }, 10);
  },
  methods: {
    getClass(item) {
      switch (item.title) {
        case "Jira Server":
          return "jiraServer";
        case "Jira Cloud":
          return "jiraCloud";
        case "Azure DevOps":
          return "azureDevOps";
        case "Github":
          return "github";
        case "Gitlab":
          return "gitlab";
      }
    },
  }
});
</script>

<style scoped lang="scss">
.row {
  width: 100vw;
  overflow: hidden;
}

.wrapper {
  display: flex;
  flex-wrap: nowrap;
  width: auto;
  transform: scale(1);
  will-change: transform;
}

.set {
  border-radius: 14px;
  display: flex;
  flex-shrink: 0;
  min-height: 262px;
  max-width: 25vw;
  min-width: 262px;
  margin: 0 0 16px 16px;
}

.connectorCards {
  max-width: 75%;
  border: none !important;
}


.jiraServer {
  background: rgba(0, 82, 204, 1);
  color: white;
  border: none;
}

.jiraServerPic {
  margin-left: 0.5vw;
  margin-bottom: 0.25vw;
  height: 50px;
  width: 100px;
  border-style: solid;
  border-color: white;
  background-color: white;
  border-radius: 0.5em;
}

.jiraCloud {
  background: rgba(0, 82, 204, 1);
  color: white;
  border: none;
}

.jiraCloudPic {
  margin-left: 0.5vw;
  margin-bottom: 0.25vw;
  height: 50px;
  width: 100px;
  border-style: solid;
  border-color: white;
  background-color: white;
  border-radius: 0.5em;
}

.azureDevOps {
  background: rgba(137,196,255, 1);
  color: white;
}

.azureDevOpsPic {
  margin-left: 0.5vw;
  margin-bottom: 0.25vw;
  height: 50px;
  width: 87.5px;
  border-style: solid;
  border-color: white;
  background-color: white;
  border-radius: 0.5em;
}

.github {
  color: white;
  background-color: #525252;
}

.githubPic {
  margin-left: 0.5vw;
  margin-bottom: 0.25vw;
  height: 50px;
  width: 50px;
  border-style: solid;
  border-color: white;
  background-color: white;
  border-radius: 0.5em;
}

.gitlab {
  background: linear-gradient(90deg, rgba(252, 161, 33, 1) 0%, rgba(252, 109, 38, 1) 35%, rgba(226, 67, 41,1), rgba(169, 137, 245, 1) 100%);
  color: white;
  border: none;
}

.gitlabPic {
  margin-left: 0.5vw;
  margin-bottom: 0.25vw;
  height: 50px;
  width: 50px;
  border-style: solid;
  border-color: white;
  background-color: white;
  border-radius: 0.5em;
}

.title {
  color: white
}
</style>
