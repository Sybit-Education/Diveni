<template>
  <div>
    <b-row>
      <b-col>
        <div class="d-flex justify-content-around">
          <div
            v-for="item of userStoryMode === jiraTag ? allCardSetsWithJiraMode : allCardSets"
            :key="item.name"
            class="swipe-card"
            :class="[selectedCardSet.name === item.name ? 'selectedCard' : 'inActiveCard', item.position === 1 ? 'fibo' : '',
                    item.position === 2 ? 'shirt' : '', item.position === 3 ? 'hourEstimation' : '',
                    item.position === 4 ? 'numbers' : '', item.position === 5 ? 'ownSet': '']"
            @click="onCardSetSelected(item)"
          >
            <div id="text" style="padding-top: 16px; color: black">
              {{ item.name }}
            </div>
            <div style="padding: 16px; text-align: center; color: black">
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
          style="width: 60px"
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
            pill
            style="min-width: 60px"
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
  methods: {
    isActiveCardSetNumber(num) {
      return this.selectedCardSet.activeValues.includes(num) ? "activePills m-1" : "outline-secondary m-1";
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
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

.swipe-card {
  width: 175px;
  height: 230px;
  justify-content: flex-start; /* Centering y-axis 202 */
  align-items: center; /* Centering x-axis 168 */
  border-radius: 5%;
  display: flex;
  flex-direction: column;
  cursor: pointer;
}

.selectedCard {
  border-width: 0.25em;
  border-color: var(--preparePageActiveCardSet) !important;
  border-style: solid;
}

.inActiveCard:hover {
  border-width: large;
  border-color: var(--preparePageInActiveCardSet);
  border-style: solid;
  width: 200px;
  height:245px;
}

.fibo {
  background-image: url("@/assets/Fibonacci.png");
  background-size: cover;
}

.shirt{
  background-image: url("@/assets/TShirtSize.png");
  background-size: cover;
}

.hourEstimation {
  background-image: url("@/assets/HourEstimation.png");
  background-size: cover;
}

.numbers {
  background-image: url("@/assets/numberEstimation.png");
  background-size: cover;
}

.ownSet {
  background-image: url("@/assets/ownSetEstimation.png");
  background-size: cover;
}

#text {
  font-size: 20px;
  font-weight: 500;
}
#popUp {
  background-color: var(--landingPageCardsBackground);
}

.activePills {
  background-color: var(--preparePageMainColor) !important;
  color: var(--text-primary-color);
}

.activePills:hover {
  background-color: var(--joinButtonHovered) !important;
  color: var(--text-primary-color);
}

.pillPosition {
  margin-left: auto;
  margin-right: auto;
}
</style>
