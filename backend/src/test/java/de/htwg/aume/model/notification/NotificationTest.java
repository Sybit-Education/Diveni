package de.htwg.aume.model.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

public class NotificationTest {

    @Test
    public void adminLeftNotification_correctJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String expectedJson = String.format("{\"type\":\"%s\",\"payload\":null}", NotificationType.ADMIN_LEFT.name());
        assertEquals(expectedJson, mapper.writeValueAsString(new Notification(NotificationType.ADMIN_LEFT, null)));
    }
}
