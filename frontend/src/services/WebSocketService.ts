import { Client, StompSubscription, TickerStrategy, ReconnectionTimeMode } from "@stomp/stompjs";
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
  private activeStompSubs = new Map<string, StompSubscription>();
  private wasConnected = false;
  readonly maxRetries = 10;
  private lastUrl: string | null = null;

  public connectionState: Ref<ConnectionState> = ref(ConnectionState.DISCONNECTED);
  public retryCount: Ref<number> = ref(0);
  /** True when disconnect was triggered by user action, not connection loss. */
  public intentionalDisconnect = false;
  /** Optional callback invoked when the broker sends a STOMP ERROR frame. */
  public onStompErrorCallback: ((message: string) => void) | null = null;

  get retriesExhausted(): boolean {
    return (
      this.retryCount.value > this.maxRetries &&
      this.connectionState.value === ConnectionState.DISCONNECTED
    );
  }

  connect(url: string): void {
    this.lastUrl = url;
    this.retryCount.value = 0;

    // Tear down previous client; deactivate in background -- callbacks are
    // guarded below so the old client's late onDisconnect/onWebSocketClose
    // cannot mutate state after the new client takes over.
    const oldClient = this.client;
    this.client = null;
    this.activeStompSubs.clear();
    this.wasConnected = false;
    this.intentionalDisconnect = false;
    if (oldClient?.active) {
      void oldClient.deactivate();
    }

    const brokerURL = this.httpToWs(url);
    this.connectionState.value = ConnectionState.CONNECTING;

    const newClient = new Client({
      brokerURL,

      reconnectDelay: 1000,
      reconnectTimeMode: ReconnectionTimeMode.EXPONENTIAL,
      maxReconnectDelay: 30000,

      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      heartbeatStrategy: TickerStrategy.Worker,
      heartbeatToleranceMultiplier: 2,

      connectionTimeout: 10000,
      discardWebsocketOnCommFailure: true,

      debug: import.meta.env.DEV ? (msg: string) => console.debug("[STOMP]", msg) : () => {},

      beforeConnect: () => {
        if (this.client !== newClient) return;
        if (this.wasConnected) {
          this.retryCount.value++;
          if (this.retryCount.value > this.maxRetries) {
            newClient.deactivate();
            this.connectionState.value = ConnectionState.DISCONNECTED;
            return;
          }
          this.connectionState.value = ConnectionState.RECONNECTING;
        }
      },

      onConnect: () => {
        if (this.client !== newClient) return;
        this.retryCount.value = 0;
        this.wasConnected = true;
        this.connectionState.value = ConnectionState.CONNECTED;
        this.resubscribeAll();
      },

      onStompError: (frame) => {
        if (this.client !== newClient) return;
        const message = frame.headers["message"] ?? frame.body;
        console.error("[WebSocket] STOMP error:", message);
        this.onStompErrorCallback?.(message);
      },

      onWebSocketError: (event) => {
        if (this.client !== newClient) return;
        console.error("[WebSocket] Transport error:", event);
      },

      onWebSocketClose: (event) => {
        // Ignore events from a stale client after connect() replaced it
        if (this.client !== newClient) return;
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
        // Ignore events from a stale client after connect() replaced it
        if (this.client !== newClient) return;
        this.connectionState.value = ConnectionState.DISCONNECTED;
      },
    });

    this.client = newClient;
    newClient.activate();
  }

  reconnect(): void {
    if (this.lastUrl) {
      this.connect(this.lastUrl);
    }
  }

  async disconnect(): Promise<void> {
    this.intentionalDisconnect = true;
    this.onStompErrorCallback = null;
    this.activeStompSubs.clear();
    this.subscriptions.clear();
    this.wasConnected = false;
    // Capture current client before awaiting -- a concurrent connect() may
    // replace this.client while deactivate() is in-flight
    const clientToDisconnect = this.client;
    if (clientToDisconnect) {
      await clientToDisconnect.deactivate();
      // Only null if no new connect() replaced it while we were awaiting
      if (this.client === clientToDisconnect) {
        this.client = null;
      }
    }
    // Only update state if no new connect() took over
    if (!this.client || this.client === clientToDisconnect) {
      this.connectionState.value = ConnectionState.DISCONNECTED;
    }
  }

  subscribe(destination: string, callback: (body: string) => void): () => void {
    // If already subscribed to this destination, unsubscribe the old STOMP sub first
    const existingSub = this.activeStompSubs.get(destination);
    if (existingSub) {
      existingSub.unsubscribe();
      this.activeStompSubs.delete(destination);
    }

    this.subscriptions.set(destination, callback);

    if (this.client?.connected) {
      const sub = this.client.subscribe(destination, (message) => {
        callback(message.body);
      });
      this.activeStompSubs.set(destination, sub);
    }

    return () => {
      this.subscriptions.delete(destination);
      const sub = this.activeStompSubs.get(destination);
      if (sub) {
        sub.unsubscribe();
        this.activeStompSubs.delete(destination);
      }
    };
  }

  publish(destination: string, body?: string | number): void {
    if (!this.client?.connected) {
      console.warn("[WebSocket] Cannot publish - not connected");
      return;
    }
    this.client.publish({ destination, body: body != null ? String(body) : "" });
  }

  private resubscribeAll(): void {
    if (!this.client?.connected) return;
    // Old StompSubscription refs are dead after reconnect -- clear them
    this.activeStompSubs.clear();

    for (const [destination, callback] of this.subscriptions) {
      const sub = this.client.subscribe(destination, (message) => {
        callback(message.body);
      });
      this.activeStompSubs.set(destination, sub);
    }
  }

  private httpToWs(url: string): string {
    return url.replace(/^http/, "ws");
  }
}

export const webSocketService = new WebSocketService();
