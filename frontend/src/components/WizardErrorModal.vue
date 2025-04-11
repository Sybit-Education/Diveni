<template>
  <teleport to="body">
    <div v-if="visible" class="modal-backdrop">
      <div class="modal-content">
        <h2 class="modal-title">{{ title }}</h2>
        <p class="modal-message">{{ message }}</p>
        <button class="btn btn-primary" @click="handleOk">OK</button>
      </div>
    </div>
  </teleport>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "WizardErrorModal",
  props: {
    visible: { type: Boolean, required: false, default: false },
    title: { type: String, default: "", required: false },
    message: { type: String, default: "", required: false },
  },
  emits: ["close"],
  methods: {
    handleOk() {
      this.$emit("close");
      window.location.href = "/prepare";
    },
  },
});
</script>

<style scoped>
.modal-backdrop {
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 1;
  background-color: rgba(0, 0, 0, 0.75);
}

.modal-content {
  border-radius: 12px;
  padding: 2rem;
  max-width: 500px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.4);
  text-align: center;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: var(--text-primary-color);
}

.modal-message {
  font-size: 1rem;
  margin-bottom: 1.5rem;
  color: var(--text-primary-color);
}
</style>
