<template>
  <div
    id="privacyModal"
  >
    <b-modal
      v-model="showModal"
      centered
      hide-header-close
    >
      <template #modal-header>
        <div class="text-center">
          Mark confidential information
        </div>
      </template>
      <BIconExclamationTriangle/> Warning <BIconExclamationTriangle/>: Your User Story will be sent to OpenAI's GPT. Mark confidential Information now.
      <div>
        <UiToastEditorWrapper
          :initial-value="currentText"
          :none-clickable="true"
          :class="isCompany || isNumber || isPerson ? 'confidential-modal' : ''"
        />
        <div class="d-flex justify-content-between">
          <b-button
            class="toggle-button"
            :class="isCompany ? 'activeCompany' : ''"
            @click="markButtonClicked('company'); $event.target.blur()"
          >
            <BIconHouseDoorFill/> Companies
          </b-button>
          <b-button
            class="toggle-button"
            :class="isPerson ? 'activePerson' : ''"
            @click="markButtonClicked('person')"
          >
            <BIconPersonFill/> Personal Information
          </b-button>
          <b-button
            class="toggle-button"
            :class="isNumber ? 'activeNumber' : ''"
            @click="markButtonClicked('number')"
          >
            <BIconCalculatorFill/> Numbers
          </b-button>
        </div>
        <div class="mt-2">
          <b-button
            id="addWordsToListButton"
            @click="addWordToList()"
          >
            <BIconPlus/> Add
          </b-button>
        </div>
        <div
          class="text-center"
        >
          <div
            v-for="(data, index) in confidentialWords.keys()"
            :key="data"
            class="d-inline-block"
          >
            <div
              id="confidential-words"
              class="mx-2"
              @mouseover="hover = index"
              @mouseleave="hover = null"
            >
              <BIconHouseFill v-if="confidentialWords.get(data) === 'company'"/>
              <BIconPersonFill v-if="confidentialWords.get(data) === 'person'"/>
              <BIconCalculatorFill v-if="confidentialWords.get(data) === 'number'"/>
              {{data}}

              <b-button
                variant="outline-danger"
                class="border-0"
                size="sm"
                @click="confidentialWords.delete(data)"
              >
                <b-icon-trash />
              </b-button>
            </div>
          </div>
        </div>
      </div>
      <div v-if="isDescription" class="my-2 d-inline-flex">
        <div class="position-relative mb-2">
          <input class="position-absolute" style="height: 20px; width:20px; left: 150px; top: 2px;" v-model="selectedLanguage" type="radio" name="languageSelector" value="english">
          <label for="english" class="position-absolute" style="left: 175px;">English</label>
        </div>
        <div class="position-relative mb-2">
          <input class="position-absolute" style="height: 20px; width:20px; left: 250px; top: 2px;" v-model="selectedLanguage" type="radio" name="languageSelector" value="german">
          <label for="german" style="left: 275px" class="position-absolute">German</label>
        </div>
      </div>
      <template #modal-footer >
        <div id="aiOptions" class="text-center mt-1">
          <b-button id="acceptAIOption" class="m-1" @click="submitIssue">
            <b-icon-check2 />
            Ok
          </b-button>
          <b-button class="aiOptionButtons m-1" @click="hideModal(); $event.target.blur();">
            <b-icon-x-square/>
            Cancel
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
    isDescription: { type: Boolean, required: false, default: false }
  },
  setup() {
    const { t } = useI18n();
    return { t };
  },
  data() {
    return {
      confidentialWords: {} as Map<string, string>,
      hover: null,
      isCompany: false,
      isPerson: false,
      isNumber: false,
      showModal: true,
      selectedLanguage: "english",
    }
  },
  created() {
    this.confidentialWords = new Map();
  },
  methods: {
    markButtonClicked(type) {
      switch (type) {
        case 'company':
          this.isCompany = !this.isCompany;
          this.isPerson = false;
          this.isNumber = false;
          document.documentElement.style.setProperty("--markConfidentialInformation", "red");
          break;
        case 'person':
          this.isPerson = !this.isPerson;
          this.isCompany = false;
          this.isNumber = false;
          document.documentElement.style.setProperty("--markConfidentialInformation", "green");
          break;
        case 'number':
          this.isNumber = !this.isNumber;
          this.isCompany = false;
          this.isPerson = false;
          document.documentElement.style.setProperty("--markConfidentialInformation", "#053C5E");
          break;
      }
    },
    addWordToList() {
      const selection = window.getSelection()
      if (selection !== null && selection.toString() !== '' && !this.confidentialWords.has(selection.toString())) {
        if (this.isCompany) {
          this.confidentialWords.set(selection.toString(), "company");
        }
        if (this.isPerson) {
          this.confidentialWords.set(selection.toString(), "person");
        }
        if (this.isNumber) {
          this.confidentialWords.set(selection.toString(), "number");
        }
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
.confidential-modal {
  .toastui-editor-contents p::selection {
    color: var(--text-primary-color);
    background-color: var(--markConfidentialInformation);
    font-size: large;
  }
}

#addWordsToListButton {
  width: 100%;
}

.toggle-button {
  background-color: #0074d9 !important;
  color: #ffffff !important;
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.toggle-button:focus {
  color: white;
}

.toggle-button.activeCompany {
  transform: translateY(5px);
  background-color: red !important;
  /* Add any other styles for the "in use" state */
}

.toggle-button.activePerson {
  transform: translateY(5px);
  background-color: green !important;
  /* Add any other styles for the "in use" state */
}

.toggle-button.activeNumber {
  transform: translateY(5px);
  background-color: #053C5E !important;
  /* Add any other styles for the "in use" state */
}
</style>
