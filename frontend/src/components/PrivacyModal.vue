<template>
  <div
    id="privacyModal"
  >
    <b-modal
      v-model="showModal"
      centered
      hide-header-close
      @hide="hideModal"
    >
      <template #modal-header>
        <div class="text-center">
          {{ t("general.aiFeature.privacyModal.title") }}
        </div>
      </template>
      <div id="warningText"> <BIconExclamationTriangle/> {{ t("general.aiFeature.privacyModal.warning") }}: {{ t("general.aiFeature.privacyModal.warningInfo") }}</div>
      <div>
        <b-form-textarea
          v-if="currentTitle !== ''"
          id="titleInputField"
          class="mt-2"
          v-model="currentTitleData"
          disabled/>
        <UiToastEditorWrapper
          :initial-value="currentText"
          :none-clickable="true"
        />
        <div class="d-flex justify-content-between">
          <b-button
            class="toggle-button"
            @click="addWordToList('company'); $event.target.blur()"
          >
            <BIconHouseDoorFill/> {{ t("general.aiFeature.privacyModal.buttons.company") }}
          </b-button>
          <b-button
            class="toggle-button"
            @click="addWordToList('person'); $event.target.blur()"
          >
            <BIconPersonFill/> {{ t("general.aiFeature.privacyModal.buttons.person") }}
          </b-button>
          <b-button
            class="toggle-button"
            @click="addWordToList('number'); $event.target.blur()"
          >
            <BIconCalculatorFill/> {{ t("general.aiFeature.privacyModal.buttons.number") }}
          </b-button>
        </div>
        <div
          class="d-flex justify-content-between mt-3"
        >
          <div class="confidential-words-div">
            <div
              v-for="(data, index) in confidentialWords.keys()"
              :key="data"
            >
              <div
                v-if="confidentialWords.get(data) === 'company'"
                @mouseover="hover = index"
                @mouseleave="hover = null"
              >
                <BIconHouseFill/>
                {{data}}

                <b-button
                  variant="outline-danger"
                  class="border-0"
                  size="sm"
                  @click="confidentialWords.delete(data);
                caseSensitiveList = caseSensitiveList.filter(word => word !== data.toLowerCase());"
                >
                  <b-icon-trash />
                </b-button>
              </div>
            </div>
          </div>
          <div class="confidential-words-div">
            <div
              v-for="(data, index) in confidentialWords.keys()"
              :key="data"
            >
              <div
                v-if="confidentialWords.get(data) === 'person'"
                class="mx-2"
                @mouseover="hover = index"
                @mouseleave="hover = null"
              >
                <BIconPersonFill/>
                {{data}}

                <b-button
                  variant="outline-danger"
                  class="border-0"
                  size="sm"
                  @click="confidentialWords.delete(data);
                caseSensitiveList = caseSensitiveList.filter(word => word !== data.toLowerCase());"
                >
                  <b-icon-trash />
                </b-button>
              </div>
            </div>
          </div>
          <div class="confidential-words-div">
            <div
              v-for="(data, index) in confidentialWords.keys()"
              :key="data"
            >
              <div
                v-if="confidentialWords.get(data) === 'number'"
                class="mx-2"
                @mouseover="hover = index"
                @mouseleave="hover = null"
              >
                <BIconCalculatorFill/>
                {{data}}

                <b-button
                  variant="outline-danger"
                  class="border-0"
                  size="sm"
                  @click="confidentialWords.delete(data);
                caseSensitiveList = caseSensitiveList.filter(word => word !== data.toLowerCase());"
                >
                  <b-icon-trash />
                </b-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="isDescription" class="my-2 d-inline-flex">
        <div class="position-relative mb-2">
          <input id="english-radio-button" class="position-absolute" v-model="selectedLanguage" type="radio" name="languageSelector" value="english">
          <label for="english" id="english-label" class="position-absolute">{{ t("general.aiFeature.privacyModal.labels.english") }}</label>
        </div>
        <div class="position-relative mb-2">
          <input id="german-radio-button" class="position-absolute" v-model="selectedLanguage" type="radio" name="languageSelector" value="german">
          <label for="german" id="german-label" class="position-absolute">{{ t("general.aiFeature.privacyModal.labels.german") }}</label>
        </div>
      </div>
      <template #modal-footer >
        <div id="aiOptions" class="text-center mt-1">
          <b-button id="acceptAIOption" class="m-1" @click="submitIssue">
            <b-icon-check2 />
            {{ t("general.aiFeature.privacyModal.buttons.ok") }}
          </b-button>
          <b-button class="aiOptionButtons m-1" @click="hideModal(); $event.target.blur();">
            <b-icon-x-square/>
            {{ t("general.aiFeature.privacyModal.buttons.cancel") }}
          </b-button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts">

import {defineComponent} from "vue";
import UiToastEditorWrapper from "@/components/UiToastEditorWrapper.vue";
import {useI18n} from "vue-i18n";

export default defineComponent({
  name: "PrivacyModal",
  components: {UiToastEditorWrapper},
  props: {
    currentText: {type: String, required: false, default: ""},
    currentTitle: {type: String, required: false, default: ""},
    isDescription: { type: Boolean, required: false, default: false }
  },
  setup() {
    const { t } = useI18n();
    return { t };
  },
  data() {
    return {
      confidentialWords: {} as Map<string, string>,
      caseSensitiveList: [] as Array<string>,
      hover: null,
      showModal: true,
      selectedLanguage: "english",
      currentTitleData: "",
    }
  },
  created() {
    this.confidentialWords = new Map();
    this.currentTitleData = this.currentTitle;
  },
  methods: {
    addWordToList(type: string) {
      const selection = window.getSelection();
      if (selection !== null && selection.toString().trim() !== '' && !this.caseSensitiveList.includes(selection.toString().trim().toLowerCase())
        && (this.currentText.includes(selection.toString().trim()) || this.currentTitle.includes(selection.toString().trim()))) {
        this.confidentialWords.set(selection.toString().trim(), type);
        this.caseSensitiveList.push(selection.toString().trim().toLowerCase())
      }
    },
    submitIssue() {
      if (this.isDescription) {
        this.$emit("sendGPTRequest", {description: this.currentText, confidentialData: this.confidentialWords, language: this.selectedLanguage });
      } else {
        this.$emit("sendGPTRequest", {confidentialData: this.confidentialWords });
      }
      this.hideModal();
    },
    hideModal() {
      this.showModal = false;
      this.$emit("resetShowModal");
    },
  }
})
</script>
<style lang="scss">
.confidential-words-div {
  display: block;
  width: 33%;
}

#warningText {
  font-style: italic;
}

#titleInputField {
  border: 0;
  background-color: var(--text-field) !important;
  color: var(--text-primary-color);
}

#addWordsToListButton {
  width: 100%;
}

#english-radio-button {
  height: 20px;
  width:20px;
  left: 150px;
  top: 2px;
}
#english-label {
  left: 175px;
}
#german-radio-button {
  height: 20px;
  width:20px;
  left: 250px;
  top: 2px;
}
#german-label {
  left: 275px
}
</style>
