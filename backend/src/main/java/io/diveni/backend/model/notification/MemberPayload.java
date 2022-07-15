package io.diveni.backend.model.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberPayload extends NotificationPayload {
  private final String memberID;
}
