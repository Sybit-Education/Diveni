/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import io.diveni.backend.model.Session;
import io.diveni.backend.principals.MemberPrincipal;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InactiveMemberCleanupTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(InactiveMemberCleanupTask.class);
  private static final Duration GRACE_PERIOD = Duration.ofMinutes(3);

  @Autowired private DatabaseService databaseService;
  @Autowired private WebSocketService webSocketService;

  @Scheduled(fixedRate = 60_000)
  public void cleanupStaleInactiveMembers() {
    Instant cutoff = Instant.now().minus(GRACE_PERIOD);

    for (Session session : databaseService.getSessions()) {
      Session cleaned = session.removeStaleInactiveMembers(cutoff);
      if (cleaned != session) {
        databaseService.saveSession(cleaned);
        session.getMembers().stream()
            .filter(
                m ->
                    !m.isActive()
                        && m.getDeactivatedAt() != null
                        && m.getDeactivatedAt().isBefore(cutoff))
            .forEach(
                m ->
                    webSocketService.removeMemberPrincipal(
                        new MemberPrincipal(session.getSessionID(), m.getMemberID())));
        webSocketService.sendMembersUpdate(cleaned);
        LOGGER.info(
            "Cleaned {} stale members from session {}",
            session.getMembers().size() - cleaned.getMembers().size(),
            session.getSessionID());
      }
    }
  }
}
