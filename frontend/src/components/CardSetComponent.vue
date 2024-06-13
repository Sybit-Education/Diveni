<template>
  <div>
    <b-row :class="isMobile ? 'd-flex justify-content-center' : 'd-flex justify-content-around'">
      <b-col
        v-for="item of userStoryMode === jiraTag ? allCardSetsWithJiraMode : allCardSets"
        :key="item.position"
        :cols="isMobile ? '3' : ''"
        class="card m-2"
        :class="getClasses(item)"
        @click="onCardSetSelected(item)"
      >
        <div class="card-title">
          {{ getTitle(item.position, userStoryMode !== jiraTag) }}
        </div>
        <div class="card-description">
          {{ getDescription(item.position, userStoryMode !== jiraTag) }}
          <div v-if="item.values.length === 0">
            <span id="createSetHint">
              <b-icon-info-circle class="mt-3 me-1" />{{
                t("session.prepare.step.selection.cardSet.sets.ownSet.hint.label")
              }}</span
            >
            <b-popover id="popUp" target="createSetHint" triggers="hover" placement="top">
              <template #title>
                {{ t("session.prepare.step.selection.cardSet.sets.ownSet.hint.label") }}
              </template>
              <p>
                {{ t("session.prepare.step.selection.cardSet.sets.ownSet.hint.description") }}
              </p>
              <p>
                {{ t("session.prepare.step.selection.cardSet.sets.ownSet.hint.example") }}
              </p>
            </b-popover>
          </div>
        </div>
      </b-col>
    </b-row>
    <b-row v-if="changedCardSet.name !== ''">
      <div v-if="changedCardSet.values.length !== 0" class="text-center mt-3 pill-group">
        <b-button
          v-for="item in changedCardSet.values"
          :key="item"
          :class="isActiveCardSetNumber(item)"
          pill
          class="pill m-2"
          @click="
            onCardSetNumberSelected(item);
            $event.target.blur();
          "
        >
          {{ item }}
        </b-button>
      </div>
      <b-row v-else class="mt-3 pill-group">
        <b-col sm="6">
          <b-form-input
            v-model="createSetInput"
            :placeholder="t('session.prepare.step.selection.cardSet.sets.ownSet.hint.example')"
          ></b-form-input>
        </b-col>
        <b-col sm="6">
          <b-button
            v-for="item in changedCardSet.activeValues"
            :key="item"
            :class="isActiveCardSetNumber(item)"
            class="pill"
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
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";
import CardSet from "../model/CardSet";

export default defineComponent({
  name: "CardSetComponent",
  props: {
    userStoryMode: { type: String, required: true },
    selectedCardSet: {
      type: Object,
      required: true,
    },
  },
  setup() {
    const { t } = useI18n();
    return { t };
  },
  data() {
    return {
      theme: localStorage.getItem("user-theme"),
      jiraTag: "US_JIRA",
      createSetInput: "",
      changedCardSet: {
        name: "",
        values: [] as string[],
        activeValues: [] as string[],
        position: 0,
      } as CardSet,
      allCardSets: [
        {
          values: ["1", "2", "3", "5", "8", "13", "21", "34", "55", "?"],
          activeValues: ["1", "2", "3", "5", "8", "13", "21"],
          position: 1,
        },
        {
          values: ["XXS", "XS", "S", "M", "L", "XL", "XXL", "XXXL", "?"],
          activeValues: ["XS", "S", "M", "L", "XL"],
          position: 2,
        },
        {
          values: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16", "?"],
          activeValues: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16"],
          position: 3,
        },
        {
          values: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "?"],
          activeValues: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
          position: 4,
        },
        {
          values: [],
          activeValues: [],
          position: 5,
        },
      ],
      allCardSetsWithJiraMode: [
        {
          values: ["1", "2", "3", "5", "8", "13", "21", "34", "55", "?"],
          activeValues: ["1", "2", "3", "5", "8", "13", "21"],
          position: 1,
        },
        {
          values: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16", "?"],
          activeValues: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16"],
          position: 2,
        },
        {
          values: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "?"],
          activeValues: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
          position: 3,
        },
      ],
    };
  },
  computed: {
    isMobile() {
      return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
        navigator.userAgent
      );
    },
  },
  watch: {
    createSetInput(newVal) {
      if (newVal.length > 1 && newVal.slice(-1) === ";") {
        const currentValues = [...new Set(newVal.split(";"))] as string[];
        currentValues.pop();
        this.changedCardSet.activeValues = currentValues;
        this.$emit("selectedCardSetOptions", this.changedCardSet);
      } else if (newVal.length == 0) {
        this.changedCardSet.activeValues = [];
        this.$emit("selectedCardSetOptions", this.changedCardSet);
      }
    },
  },
  mounted() {
    window.addEventListener("user-theme-localstorage-changed", (event) => {
      const customEvent = event as CustomEvent;
      this.theme = customEvent.detail.storage;
    });
    this.changedCardSet = this.selectedCardSet as CardSet;
  },
  methods: {
    getTitle(pos, mode) {
      if (mode) {
        switch (pos) {
          case 1:
            return this.t("session.prepare.step.selection.cardSet.sets.fibonacci.label");
          case 2:
            return this.t("session.prepare.step.selection.cardSet.sets.tShirtSizes.label");
          case 3:
            return this.t("session.prepare.step.selection.cardSet.sets.hours.label");
          case 4:
            return this.t("session.prepare.step.selection.cardSet.sets.numbers.label");
          case 5:
            return this.t("session.prepare.step.selection.cardSet.sets.ownSet.label");
        }
      } else {
        switch (pos) {
          case 1:
            return this.t("session.prepare.step.selection.cardSet.sets.fibonacci.label");
          case 2:
            return this.t("session.prepare.step.selection.cardSet.sets.hours.label");
          case 3:
            return this.t("session.prepare.step.selection.cardSet.sets.numbers.label");
        }
      }
    },
    getDescription(pos, mode) {
      if (mode) {
        switch (pos) {
          case 1:
            return this.t("session.prepare.step.selection.cardSet.sets.fibonacci.description");
          case 2:
            return this.t("session.prepare.step.selection.cardSet.sets.tShirtSizes.description");
          case 3:
            return this.t("session.prepare.step.selection.cardSet.sets.hours.description");
          case 4:
            return this.t("session.prepare.step.selection.cardSet.sets.numbers.description");
          case 5:
            return this.t("session.prepare.step.selection.cardSet.sets.ownSet.description");
        }
      } else {
        switch (pos) {
          case 1:
            return this.t("session.prepare.step.selection.cardSet.sets.fibonacci.description");
          case 2:
            return this.t("session.prepare.step.selection.cardSet.sets.hours.description");
          case 3:
            return this.t("session.prepare.step.selection.cardSet.sets.numbers.description");
        }
      }
    },
    isActiveCardSetNumber(num) {
      return this.changedCardSet.activeValues.includes(num) ? "active" : "outline-secondary";
    },
    onCardSetSelected(set) {
      this.changedCardSet = set;
      this.emitChanges();
    },
    emitChanges() {
      this.$emit("selectedCardSetOptions", this.changedCardSet);
    },
    onCardSetNumberSelected(number) {
      if (this.changedCardSet.activeValues.includes(number)) {
        this.changedCardSet.activeValues = this.changedCardSet.activeValues.filter(
          (num) => num !== number
        );
      } else {
        const newActiveValues = [...this.changedCardSet.activeValues, number];
        this.changedCardSet.activeValues = this.changedCardSet.values.filter((num) =>
          newActiveValues.includes(num)
        );
      }
      this.emitChanges();
    },
    getClasses(item) {
      return `${this.getCardActiveClass(item)} ${this.getPictureClass(item)}`;
    },
    getCardActiveClass(item) {
      return this.changedCardSet.position === item.position ? "selected" : "";
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
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
@import "@/assets/style/variables.scss";
.card {
  max-width: 224px;
  min-height: 300px;
  justify-content: flex-start;
  align-items: center;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  border-radius: $border-radius;
  box-shadow: 8px 8px 5px var(--box-shadow);

  &:hover {
    border-width: 4px;
    border-color: var(--preparePageInActiveTabHover);
    border-style: solid;
  }
  &.selected {
    border-width: 5px;
    border-color: var(--primary-button);
    border-style: solid;
  }

  .card-title {
    font-size: 22px;
    font-weight: 500;
    padding-top: 16px;
    text-align: center;
  }

  .card-description {
    padding: 16px;
    text-align: center;
  }
}

.light1 {
  background-image: url("@/assets/preparePage/Fibonacci-LightMode.png");
  background-size: cover;
}

.light2 {
  background-image: url("@/assets/preparePage/TShirt-LightMode.png");
  background-size: cover;
}

.light3 {
  background-image: url("@/assets/preparePage/Hour-LightMode.png");
  background-size: cover;
}

.light4 {
  background-image: url("@/assets/preparePage/Numbers-LightMode.png");
  background-size: cover;
}

.light5 {
  background-image: url("@/assets/preparePage/OwnSet-LightMode.png");
  background-size: cover;
}

.dark1 {
  background-image: url("@/assets/preparePage/Fibonacci-DarkMode.png");
  background-size: cover;
  text-shadow: #000 0 0 3px;
  -webkit-font-smoothing: antialiased;
}

.dark2 {
  background-image: url("@/assets/preparePage/TShirt-DarkMode.png");
  background-size: cover;
  text-shadow: #000 0 0 3px;
  -webkit-font-smoothing: antialiased;
}

.dark3 {
  background-image: url("@/assets/preparePage/Hour-DarkMode.png");
  background-size: cover;
  text-shadow: #000 0 0 3px;
  -webkit-font-smoothing: antialiased;
}

.dark4 {
  background-image: url("@/assets/preparePage/Numbers-DarkMode.png");
  background-size: cover;
  text-shadow: #000 0 0 3px;
  -webkit-font-smoothing: antialiased;
}

.dark5 {
  background-image: url("@/assets/preparePage/OwnSet-DarkMode.png");
  background-size: cover;
  text-shadow: #000 0 0 3px;
  -webkit-font-smoothing: antialiased;
}

#popUp {
  color: var(--text-primary-color);
  background-color: var(--landingPageCardsBackground);
}

.pill-group {
  margin-left: auto;
  margin-right: auto;

  .pill {
    width: 5rem;
    border-radius: $border-radius !important;

    &:not(.active) {
      background-color: var(--preparePageNotSelectedTabBackground) !important;
      color: var(--text-primary-color) !important;
      border: transparent !important;

      &:hover {
        color: var(--text-color-hover) !important;
        background-color: var(--preparePageInActiveTabHover) !important;
      }
    }

    &.active {
      background-color: var(--preparePageMainColor) !important;
      color: var(--text-primary-color) !important;
      border-color: var(--btn-border-color) !important;

      &:hover {
        background-color: var(--preparePageInActiveTabHover);
        color: var(--text-color-hover) !important;
      }
    }
  }
}
</style>
