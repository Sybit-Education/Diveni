package de.htwg.aume.model.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Notification {
    private final NotificationType type;
    private final NotificationPayload payload;
}