/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.handler;

import io.diveni.backend.controller.WebsocketController;
import io.diveni.backend.principals.AdminPrincipal;
import io.diveni.backend.principals.MemberPrincipal;
import io.diveni.backend.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectedListener implements ApplicationListener<SessionDisconnectEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionDisconnectedListener.class);
  @Autowired WebsocketController controller;
  @Autowired WebSocketService webSocketService;

  @Override
  public void onApplicationEvent(SessionDisconnectEvent event) {
    LOGGER.debug("--> onApplicationEvent()");
    var principal = event.getUser();
    if (principal instanceof MemberPrincipal memberPrincipal) {
      if (webSocketService.consumePendingUnregister(memberPrincipal.getMemberID())) {
        LOGGER.debug(
            "Intentional unregister for member={}, skipping", memberPrincipal.getMemberID());
      } else if (controller.isMemberInSession(principal)) {
        LOGGER.debug(
            "Network disconnect for member={}, deactivating", memberPrincipal.getMemberID());
        controller.deactivateMember(memberPrincipal);
      }
    } else if (principal instanceof AdminPrincipal adminPrincipal) {
      if (webSocketService.consumePendingAdminUnregister(adminPrincipal.getAdminID())) {
        LOGGER.debug(
            "Intentional unregister for admin={}, skipping", adminPrincipal.getAdminID());
      } else {
        LOGGER.debug(
            "Network disconnect for admin={}, notifying members",
            adminPrincipal.getAdminID());
        controller.handleAdminDisconnect(adminPrincipal);
      }
    }
    LOGGER.debug("<-- onApplicationEvent()");
  }
}
