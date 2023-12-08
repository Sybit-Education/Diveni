export interface Notification {
  type: string;
  payload: Payload;
}

export interface Payload {
  memberID: string;
}
