<template>
  <transition name="banner-fade">
    <div v-if="visible" class="connection-banner" :class="bannerClass">
      <i :class="iconClass"></i>
      {{ bannerText }}
      <button v-if="showReconnectButton" class="reconnect-button" @click="onReconnect">
        {{ t("session.connection.reconnectButton") }}
      </button>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from "vue";
import { webSocketService, ConnectionState } from "@/services/WebSocketService";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const visible = ref(false);
const bannerClass = ref("");
const bannerText = ref("");
const iconClass = ref("");

let hideTimeout: ReturnType<typeof setTimeout> | null = null;
let previousState: ConnectionState = ConnectionState.DISCONNECTED;

const showReconnectButton = computed(() => webSocketService.retriesExhausted);

function onReconnect() {
  webSocketService.reconnect();
}

watch(
  () => webSocketService.connectionState.value,
  (state) => {
    if (hideTimeout) {
      clearTimeout(hideTimeout);
      hideTimeout = null;
    }

    if (state === ConnectionState.RECONNECTING) {
      visible.value = true;
      bannerClass.value = "banner-warning";
      bannerText.value = t("session.connection.reconnectingWithCount", {
        current: webSocketService.retryCount.value,
        max: webSocketService.maxRetries,
      });
      iconClass.value = "bi bi-arrow-repeat spin-icon";
    } else if (
      state === ConnectionState.DISCONNECTED &&
      previousState !== ConnectionState.DISCONNECTED &&
      !webSocketService.intentionalDisconnect
    ) {
      visible.value = true;
      bannerClass.value = "banner-danger";
      bannerText.value = t("session.connection.disconnected");
      iconClass.value = "bi bi-wifi-off";
    } else if (
      state === ConnectionState.CONNECTED &&
      previousState === ConnectionState.RECONNECTING
    ) {
      visible.value = true;
      bannerClass.value = "banner-success";
      bannerText.value = t("session.connection.reconnected");
      iconClass.value = "bi bi-check-circle";
      hideTimeout = setTimeout(() => {
        visible.value = false;
      }, 3000);
    } else {
      visible.value = false;
    }

    previousState = state;
  }
);

onUnmounted(() => {
  if (hideTimeout) {
    clearTimeout(hideTimeout);
  }
});
</script>

<style lang="scss" scoped>
.connection-banner {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  text-align: center;
  padding: 8px 16px;
  font-weight: 600;
  font-size: 0.9rem;

  i {
    margin-right: 6px;
  }
}

.banner-warning {
  background-color: #fff3cd;
  color: #856404;
  border-bottom: 1px solid #ffc107;
}

.banner-danger {
  background-color: #f8d7da;
  color: #842029;
  border-bottom: 1px solid #dc3545;
}

.banner-success {
  background-color: #d1e7dd;
  color: #0f5132;
  border-bottom: 1px solid #198754;
}

.reconnect-button {
  margin-left: 12px;
  padding: 2px 12px;
  border: 1px solid currentColor;
  border-radius: 4px;
  background: transparent;
  color: inherit;
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;

  &:hover {
    background: rgba(0, 0, 0, 0.1);
  }
}

.spin-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.banner-fade-enter-active,
.banner-fade-leave-active {
  transition: opacity 0.3s ease;
}

.banner-fade-enter-from,
.banner-fade-leave-to {
  opacity: 0;
}
</style>
