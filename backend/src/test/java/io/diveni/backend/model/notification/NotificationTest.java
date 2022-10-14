/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

public class NotificationTest {

  @Test
  public void adminLeftNotification_correctJson() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String expectedJson =
        String.format("{\"type\":\"%s\",\"payload\":null}", NotificationType.ADMIN_LEFT.name());
    assertEquals(
        expectedJson,
        mapper.writeValueAsString(new Notification(NotificationType.ADMIN_LEFT, null)));
  }
}
