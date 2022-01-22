package de.htwg.aume.model.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberPayload extends NotificationPayload {
    private final String memberID;
}
