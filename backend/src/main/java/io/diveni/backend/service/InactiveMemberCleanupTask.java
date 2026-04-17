/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import io.diveni.backend.controller.WebsocketController;
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

  private final DatabaseService databaseService;
  private final WebSocketService webSocketService;
  private final WebsocketController websocketController;

  @Autowired
  public InactiveMemberCleanupTask(
      DatabaseService databaseService,
      WebSocketService webSocketService,
      WebsocketController websocketController) {
    this.databaseService = databaseService;
    this.webSocketService = webSocketService;
    this.websocketController = websocketController;
  }

  /**
   * Periodically removes members whose deactivation is older than {@link #GRACE_PERIOD}.
   *
   * <p>Synchronizes on the {@link WebsocketController} bean monitor, the same monitor held by all
   * {@code public synchronized} WS handler methods to serialize session mutations against
   * concurrent WS traffic and avoid a lost-update race on the unversioned {@link Session} document.
   * The lock is taken per session (not around the whole loop) so WS handlers can interleave between
   * iterations.
   */
  @Scheduled(fixedRate = 60_000)
  public void cleanupStaleInactiveMembers() {
    Instant cutoff = Instant.now().minus(GRACE_PERIOD);

    for (Session snapshot : databaseService.getSessions()) {
      try {
        synchronized (websocketController) {
          Session session = databaseService.getSessionByID(snapshot.getSessionID()).orElse(null);
          if (session == null) {
            continue;
          }
          Session cleaned = session.removeStaleInactiveMembers(cutoff);
          if (cleaned == session) {
            continue;
          }
          databaseService.saveSession(cleaned);
          session.getMembers().stream()
              .filter(
                  m ->
                      !m.isActive()
                          && m.getDeactivatedAt() != null
                          && m.getDeactivatedAt().isBefore(cutoff))
              .forEach(
                  m ->
                      removeMemberPrincipalQuietly(
                          new MemberPrincipal(session.getSessionID(), m.getMemberID())));
          sendMembersUpdateQuietly(cleaned);
          LOGGER.info(
              "Cleaned {} stale members from session {}",
              session.getMembers().size() - cleaned.getMembers().size(),
              session.getSessionID());
        }
      } catch (RuntimeException e) {
        LOGGER.warn(
            "Cleanup failed for session {}, continuing with remaining sessions",
            snapshot.getSessionID(),
            e);
      }
    }
  }

  private void removeMemberPrincipalQuietly(MemberPrincipal principal) {
    try {
      webSocketService.removeMemberPrincipal(principal);
    } catch (RuntimeException e) {
      LOGGER.debug(
          "Stale principal entry already absent for session {}, member {}",
          principal.getSessionID(),
          principal.getMemberID());
    }
  }

  private void sendMembersUpdateQuietly(Session session) {
    try {
      webSocketService.sendMembersUpdate(session);
    } catch (RuntimeException e) {
      LOGGER.debug(
          "No live WS listeners for session {}, skipping members update", session.getSessionID());
    }
  }
}
