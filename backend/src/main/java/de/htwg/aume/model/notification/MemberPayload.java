package de.htwg.aume.model.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberPayload extends NotificationPayload {
    private final String memberID;
}
