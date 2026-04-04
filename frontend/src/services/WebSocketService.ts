import { Client, TickerStrategy, ReconnectionTimeMode } from "@stomp/stompjs";
import { ref, type Ref } from "vue";

export enum ConnectionState {
  DISCONNECTED = "DISCONNECTED",
  CONNECTING = "CONNECTING",
  CONNECTED = "CONNECTED",
  RECONNECTING = "RECONNECTING",
}

class WebSocketService {
  private client: Client | null = null;
  private subscriptions = new Map<string, (body: string) => void>();
  private wasConnected = false;

  public connectionState: Ref<ConnectionState> = ref(ConnectionState.DISCONNECTED);

  async connect(url: string): Promise<void> {
    if (this.client?.active) {
      await this.client.deactivate();
    }

    const brokerURL = this.httpToWs(url);

    this.connectionState.value = ConnectionState.CONNECTING;

    this.client = new Client({
      brokerURL,

      // Reconnection: exponential backoff 200ms -> 30s cap
      reconnectDelay: 200,
      reconnectTimeMode: ReconnectionTimeMode.EXPONENTIAL,
      maxReconnectDelay: 30000,

      // Heartbeat: 10s in both directions
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      // Web Worker strategy: not throttled in background tabs (Chrome 88+)
      heartbeatStrategy: TickerStrategy.Worker,
      // Allow 2x grace period before declaring server dead (20s of silence)
      heartbeatToleranceMultiplier: 2,

      // Connection timeout: fail-fast after 10s
      connectionTimeout: 10000,

      // Aggressively close socket on heartbeat failure
      discardWebsocketOnCommFailure: true,

      // Debug logging: only in development
      debug: import.meta.env.DEV ? (msg) => console.debug("[STOMP]", msg) : () => {},

      beforeConnect: () => {
        if (this.wasConnected) {
          this.connectionState.value = ConnectionState.RECONNECTING;
          console.log("[WebSocket] Reconnecting...");
        }
      },

      onConnect: () => {
        console.log("[WebSocket] Connected");
        this.wasConnected = true;
        this.connectionState.value = ConnectionState.CONNECTED;
        this.resubscribeAll();
      },

      onStompError: (frame) => {
        console.error("[WebSocket] STOMP error:", frame.headers["message"], frame.body);
      },

      onWebSocketError: (event) => {
        console.error("[WebSocket] Transport error:", event);
      },

      onWebSocketClose: (event) => {
        console.warn(
          `[WebSocket] Closed: code=${event.code}, reason=${event.reason}, clean=${event.wasClean}`
        );
        if (this.client?.active) {
          this.connectionState.value = ConnectionState.RECONNECTING;
        } else {
          this.connectionState.value = ConnectionState.DISCONNECTED;
        }
      },

      onDisconnect: () => {
        console.log("[WebSocket] Disconnected");
      },
    });

    this.client.activate();
  }

  async disconnect(): Promise<void> {
    this.subscriptions.clear();
    this.wasConnected = false;
    if (this.client) {
      await this.client.deactivate();
      this.client = null;
    }
    this.connectionState.value = ConnectionState.DISCONNECTED;
  }

  subscribe(destination: string, callback: (body: string) => void): () => void {
    this.subscriptions.set(destination, callback);

    if (this.client?.connected) {
      this.client.subscribe(destination, (message) => {
        callback(message.body);
      });
    }

    return () => {
      this.subscriptions.delete(destination);
    };
  }

  publish(destination: string, body?: string): void {
    if (!this.client?.connected) {
      console.warn("[WebSocket] Cannot publish — not connected");
      return;
    }
    this.client.publish({ destination, body: body ?? "" });
  }

  private resubscribeAll(): void {
    if (!this.client?.connected) return;
    console.log(`[WebSocket] Re-subscribing to ${this.subscriptions.size} destinations`);
    for (const [destination, callback] of this.subscriptions) {
      this.client.subscribe(destination, (message) => {
        callback(message.body);
      });
    }
  }

  private httpToWs(url: string): string {
    return url.replace(/^http/, "ws");
  }
}

export const webSocketService = new WebSocketService();
