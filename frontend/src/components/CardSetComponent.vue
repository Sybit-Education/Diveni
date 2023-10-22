<template>
  <div>
    <b-row class="d-flex justify-content-around">
          <b-col
            cols="4"
            md="auto"
            v-for="item of userStoryMode === jiraTag ? allCardSetsWithJiraMode : allCardSets"
            :key="item.position"
            class="swipe-card my-5 mx-5"
            :class="getClasses(item)"
            @click="onCardSetSelected(item)"
          >
            <div id="text">
              {{ item.name }}
            </div>
            <div id="cardDescription">
              {{ item.description }}
              <div v-if="item.values.length === 0">
                <span id="createSetHint">
                  <b-icon-info-circle class="mt-3 me-1" />{{
                    $t("session.prepare.step.selection.cardSet.sets.ownSet.hint.label")
                  }}</span
                >
                <b-popover target="createSetHint" triggers="hover" placement="top" id="popUp">
                  <template #title>
                    {{ $t("session.prepare.step.selection.cardSet.sets.ownSet.hint.label") }}
                  </template>
                  <p>
                    {{ $t("session.prepare.step.selection.cardSet.sets.ownSet.hint.description") }}
                  </p>
                  <p>
                    {{ $t("session.prepare.step.selection.cardSet.sets.ownSet.hint.example") }}
                  </p>
                </b-popover>
              </div>
            </div>
          </b-col>
    </b-row>
    <b-row v-if="selectedCardSet.name !== ''">
      <div v-if="selectedCardSet.values.length !== 0" class="text-center mt-3 pillPosition">
        <b-button
          v-for="item in selectedCardSet.values"
          :key="item"
          :class="isActiveCardSetNumber(item)"
          pill
          class="pills"
          @click="onCardSetNumberSelected(item); $event.target.blur();"
        >
          {{ item }}
        </b-button>
      </div>
      <b-row v-else class="mt-3 d-flex px-5 pillPosition">
        <b-col sm="6">
          <b-form-input
            v-model="createSetInput"
            :placeholder="$t('session.prepare.step.selection.cardSet.sets.ownSet.hint.example')"
          ></b-form-input>
        </b-col>
        <b-col sm="6">
          <b-button
            v-for="item in selectedCardSet.activeValues"
            :key="item"
            :class="isActiveCardSetNumber(item)"
            class="pills"
            pill
          >
            {{ item }}
          </b-button>
        </b-col>
      </b-row>
    </b-row>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "CardSetComponent",
  props: {
    userStoryMode: { type: String, required: true },
  },
  data() {
    return {
      theme: localStorage.getItem('user-theme'),
      jiraTag: "US_JIRA",
      selectedCardSet: {
        name: "",
        values: [],
        activeValues: [] as string[],
      },
      createSetInput: "",
      allCardSets: [
        {
          name: this.$t("session.prepare.step.selection.cardSet.sets.fibonacci.label"),
          description: this.$t("session.prepare.step.selection.cardSet.sets.fibonacci.description"),
          values: ["1", "2", "3", "5", "8", "13", "21", "34", "55", "?"],
          activeValues: ["1", "2", "3", "5", "8", "13", "21"],
          position: 1,
        },
        {
          name: this.$t("session.prepare.step.selection.cardSet.sets.tShirtSizes.label"),
          description: this.$t(
            "session.prepare.step.selection.cardSet.sets.tShirtSizes.description"
          ),
          values: ["XXS", "XS", "S", "M", "L", "XL", "XXL", "XXXL", "?"],
          activeValues: ["XS", "S", "M", "L", "XL"],
          position: 2,
        },
        {
          name: this.$t("session.prepare.step.selection.cardSet.sets.hours.label"),
          description: this.$t("session.prepare.step.selection.cardSet.sets.hours.description"),
          values: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16", "?"],
          activeValues: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16"],
          position: 3,
        },
        {
          name: this.$t("session.prepare.step.selection.cardSet.sets.numbers.label"),
          description: this.$t("session.prepare.step.selection.cardSet.sets.numbers.description"),
          values: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "?"],
          activeValues: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
          position: 4,
        },
        {
          name: this.$t("session.prepare.step.selection.cardSet.sets.ownSet.label"),
          description: this.$t("session.prepare.step.selection.cardSet.sets.ownSet.description"),
          values: [],
          activeValues: [],
          position: 5,
        },
      ],
      allCardSetsWithJiraMode: [
        {
          name: this.$t("session.prepare.step.selection.cardSet.sets.fibonacci.label"),
          description: this.$t("session.prepare.step.selection.cardSet.sets.fibonacci.description"),
          values: ["1", "2", "3", "5", "8", "13", "21", "34", "55", "?"],
          activeValues: ["1", "2", "3", "5", "8", "13", "21"],
          position: 1,
        },
        {
          name: this.$t("session.prepare.step.selection.cardSet.sets.hours.label"),
          description: this.$t("session.prepare.step.selection.cardSet.sets.hours.description"),
          values: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16", "?"],
          activeValues: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16"],
          position: 2,
        },
        {
          name: this.$t("session.prepare.step.selection.cardSet.sets.numbers.label"),
          description: this.$t("session.prepare.step.selection.cardSet.sets.numbers.description"),
          values: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "?"],
          activeValues: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
          position: 3,
        },
      ],
    };
  },
  watch: {
    createSetInput(newVal) {
      if (newVal.length > 1 && newVal.slice(-1) === ";") {
        const currentValues = [...new Set(newVal.split(";"))] as string[];
        currentValues.pop();
        this.selectedCardSet.activeValues = currentValues;
        this.$emit("selectedCardSetOptions", this.selectedCardSet.activeValues);
      } else if (newVal.length == 0) {
        this.selectedCardSet.activeValues = [];
        this.$emit("selectedCardSetOptions", this.selectedCardSet.activeValues);
      }
    },
  },
  mounted() {
    window.addEventListener('user-theme-localstorage-changed', (event) => {
        const customEvent = event as CustomEvent;
        this.theme = customEvent.detail.storage;
    });
  },
  methods: {
    isActiveCardSetNumber(num) {
      return this.selectedCardSet.activeValues.includes(num) ? "activePills m-1" : "outline-secondary inActivePills m-1";
    },
    onCardSetSelected(set) {
      this.selectedCardSet = set;
      this.emitChanges();
    },
    emitChanges() {
      this.$emit("selectedCardSetOptions", this.selectedCardSet.activeValues);
    },
    onCardSetNumberSelected(number) {
      if (this.selectedCardSet.activeValues.includes(number)) {
        this.selectedCardSet.activeValues = this.selectedCardSet.activeValues.filter(
          (num) => num !== number
        );
      } else {
        const newActiveValues = [...this.selectedCardSet.activeValues, number];
        this.selectedCardSet.activeValues = this.selectedCardSet.values.filter((num) =>
          newActiveValues.includes(num)
        );
      }
      this.emitChanges();
    },
    getClasses(item) {
      return `${this.getCardActiveClass(item)} ${this.getPictureClass(item)}`;
    },
    getCardActiveClass(item) {
      return this.selectedCardSet.name === item.name ? 'selectedCard' : 'inActiveCard';
    },
    getPictureClass(item) {
      if (this.userStoryMode !== this.jiraTag) {
        return this.theme + item.position;
      }
      switch (item.position) {
        case 1:
          return this.theme + item.position;
        case 2:
          return this.theme + "3";
        case 3:
          return this.theme + "4";
        default:
          break;
      }
    }
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

.swipe-card {
  width: 226px;
  height: 303px;
  justify-content: flex-start; /* Centering y-axis 202 */
  align-items: center; /* Centering x-axis 168 */
  display: flex;
  flex-direction: column;
  cursor: pointer;
}

.selectedCard {
  border-color: var(--preparePageActiveCardSet) !important;
  border-style: solid;
  border-radius: var(--buttonShape);
}

.inActiveCard:hover {
  border-width: large;
  border-color: var(--preparePageInActiveCardSet);
  border-style: solid;
  border-radius: var(--buttonShape);
}

.light-theme1 {
    background-image: url("@/assets/preparePage/Fibonacci-LightMode.png") !important;
    background-size: cover !important;
    border-radius: var(--buttonShape) !important;
    box-shadow: 10px 10px 5px #ccc !important;
}

.light-theme2{
  background-image: url("@/assets/preparePage/TShirt-LightMode.png");
  background-size: cover;
  border-radius: var(--buttonShape);
  box-shadow: 10px 10px 5px #ccc;
}

.light-theme3 {
  background-image: url("@/assets/preparePage/Hour-LightMode.png");
  background-size: cover;
  border-radius: var(--buttonShape);
  box-shadow: 10px 10px 5px #ccc;
}

.light-theme4 {
  background-image: url("@/assets/preparePage/Numbers-LightMode.png");
  background-size: cover;
  border-radius: var(--buttonShape);
  box-shadow: 10px 10px 5px #ccc;
}

.light-theme5 {
  background-image: url("@/assets/preparePage/OwnSet-LightMode.png");
  background-size: cover;
  border-radius: var(--buttonShape);
  box-shadow: 10px 10px 5px #ccc;
}

.dark-theme1 {
  width: 226px;
  height: 303px;
  display: block;
  position: relative;
}

.dark-theme1::after {
  background-image: url("@/assets/preparePage/Fibonacci-DarkMode.png");
  background-size: cover;
  content: "";
  opacity: 0.45;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  position: absolute;
  z-index: -1;
  border-radius: var(--buttonShape);
}

.dark-theme2 {
  width: 226px;
  height: 303px;
  display: block;
  position: relative;
}

.dark-theme2::after{
  background-image: url("@/assets/preparePage/TShirt-DarkMode.png");
  background-size: cover;
  content: "";
  opacity: 0.45;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  position: absolute;
  z-index: -1;
  border-radius: var(--buttonShape);
}

.dark-theme3 {
  width: 226px;
  height: 303px;
  display: block;
  position: relative;
}

.dark-theme3::after {
  background-image: url("@/assets/preparePage/Hour-DarkMode.png");
  background-size: cover;
  content: "";
  opacity: 0.45;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  position: absolute;
  z-index: -1;
  border-radius: var(--buttonShape);
}

.dark-theme4 {
  width: 226px;
  height: 303px;
  display: block;
  position: relative;
}

.dark-theme4::after {
  background-image: url("@/assets/preparePage/Numbers-DarkMode.png");
  background-size: cover;
  content: "";
  opacity: 0.45;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  position: absolute;
  z-index: -1;
  border-radius: var(--buttonShape);
}

.dark-theme5 {
  width: 226px;
  height: 303px;
  display: block;
  position: relative;
}

.dark-theme5::after {
  background-image: url("@/assets/preparePage/OwnSet-DarkMode.png");
  background-size: cover;
  content: "";
  opacity: 0.45;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  position: absolute;
  z-index: -1;
  border-radius: var(--buttonShape);
}

#text {
  font-size: 20px;
  font-weight: 500;
  padding-top: 16px;
}

#cardDescription{
  padding: 16px;
  text-align: center;
}

#popUp {
  background-color: var(--landingPageCardsBackground);
}

.pills {
  width: 60px;
}

.activePills {
  background-color: var(--preparePageMainColor);
  color: var(--text-primary-color);
}

.activePills:hover {
  background-color: var(--preparePageInActiveTabHover);
  color: var(--text-primary-color);
}

.activePills:focus {
  background-color: var(--preparePageInActiveTabHover) !important;
  color: var(--text-primary-color);
}

.inActivePills:focus {
  background-color: grey !important;
  color: var(--text-primary-color);
}

.pillPosition {
  margin-left: auto;
  margin-right: auto;
}
</style>
