/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.diveni.backend.Utils;
import io.diveni.backend.controller.WebsocketController;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionState;
import io.diveni.backend.principals.MemberPrincipal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class InactiveMemberCleanupTaskTest {

  @Mock private DatabaseService databaseService;
  @Mock private WebSocketService webSocketService;

  // Referenced indirectly -> constructor-injected via @InjectMocks
  @SuppressWarnings("unused")
  @Mock
  private WebsocketController websocketController;

  @InjectMocks private InactiveMemberCleanupTask cleanupTask;

  @Test
  public void cleanupStaleInactiveMembers_removesExpiredMembers() {
    val activeMember = new Member(Utils.generateRandomID(), "Alice", "#fff", null, null);
    val staleMember = inactiveMember(Instant.now().minus(Duration.ofMinutes(5)));
    val session = sessionWithMembers(activeMember, staleMember);
    when(databaseService.getSessions()).thenReturn(List.of(session));
    when(databaseService.getSessionByID(session.getSessionID())).thenReturn(Optional.of(session));

    cleanupTask.cleanupStaleInactiveMembers();

    val savedSessionCaptor = ArgumentCaptor.forClass(Session.class);
    verify(databaseService).saveSession(savedSessionCaptor.capture());
    val savedSession = savedSessionCaptor.getValue();
    assertEquals(List.of(activeMember), savedSession.getMembers());
    verify(webSocketService)
        .removeMemberPrincipal(
            new MemberPrincipal(session.getSessionID(), staleMember.getMemberID()));
    verify(webSocketService).sendMembersUpdate(savedSession);
  }

  @Test
  public void cleanupStaleInactiveMembers_skipsRecentInactive() {
    val activeMember = new Member(Utils.generateRandomID(), "Alice", "#fff", null, null);
    val recentInactive = inactiveMember(Instant.now().minus(Duration.ofMinutes(1)));
    val session = sessionWithMembers(activeMember, recentInactive);
    when(databaseService.getSessions()).thenReturn(List.of(session));
    when(databaseService.getSessionByID(session.getSessionID())).thenReturn(Optional.of(session));

    cleanupTask.cleanupStaleInactiveMembers();

    verify(databaseService, never()).saveSession(any());
    verifyNoInteractions(webSocketService);
  }

  @Test
  public void cleanupStaleInactiveMembers_handlesEmptySessionList() {
    when(databaseService.getSessions()).thenReturn(List.of());

    cleanupTask.cleanupStaleInactiveMembers();

    verify(databaseService, never()).saveSession(any());
    verifyNoInteractions(webSocketService);
  }

  @Test
  public void cleanupStaleInactiveMembers_continuesAfterPerSessionFailure() {
    val badSessionStale = inactiveMember(Instant.now().minus(Duration.ofMinutes(5)));
    val goodSessionActive = new Member(Utils.generateRandomID(), "Alice", "#fff", null, null);
    val goodSessionStale = inactiveMember(Instant.now().minus(Duration.ofMinutes(5)));
    val badSession = sessionWithMembers(badSessionStale);
    val goodSession = sessionWithMembers(goodSessionActive, goodSessionStale);

    when(databaseService.getSessions()).thenReturn(List.of(badSession, goodSession));
    when(databaseService.getSessionByID(badSession.getSessionID()))
        .thenReturn(Optional.of(badSession));
    when(databaseService.getSessionByID(goodSession.getSessionID()))
        .thenReturn(Optional.of(goodSession));
    doThrow(new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.NOT_FOUND, "principal list gone"))
        .when(webSocketService)
        .removeMemberPrincipal(
            new MemberPrincipal(badSession.getSessionID(), badSessionStale.getMemberID()));

    assertDoesNotThrow(() -> cleanupTask.cleanupStaleInactiveMembers());

    verify(databaseService).saveSession(
        org.mockito.ArgumentMatchers.argThat(
            s -> s.getSessionID().equals(goodSession.getSessionID())
                && s.getMembers().size() == 1));
    verify(webSocketService)
        .removeMemberPrincipal(
            new MemberPrincipal(goodSession.getSessionID(), goodSessionStale.getMemberID()));
  }

  @Test
  public void cleanupStaleInactiveMembers_tolerates_missingPrincipalEntry_whenRemovingMember() {
    val stale = inactiveMember(Instant.now().minus(Duration.ofMinutes(5)));
    val session = sessionWithMembers(stale);
    when(databaseService.getSessions()).thenReturn(List.of(session));
    when(databaseService.getSessionByID(session.getSessionID())).thenReturn(Optional.of(session));
    doThrow(new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.NOT_FOUND, "principal list gone"))
        .when(webSocketService)
        .removeMemberPrincipal(any());

    assertDoesNotThrow(() -> cleanupTask.cleanupStaleInactiveMembers());

    verify(databaseService).saveSession(any());
    verify(webSocketService).sendMembersUpdate(any());
  }

  @Test
  public void cleanupStaleInactiveMembers_skipsWhenSessionDeletedConcurrently() {
    val session =
        sessionWithMembers(new Member(Utils.generateRandomID(), "Alice", "#fff", null, null));
    when(databaseService.getSessions()).thenReturn(List.of(session));
    when(databaseService.getSessionByID(session.getSessionID())).thenReturn(Optional.empty());

    cleanupTask.cleanupStaleInactiveMembers();

    verify(databaseService, never()).saveSession(any());
    verifyNoInteractions(webSocketService);
  }

  private static Member inactiveMember(Instant deactivatedAt) {
    return new Member(Utils.generateRandomID(), "Bob", "#000", null, null, false, deactivatedAt);
  }

  private static Session sessionWithMembers(Member... members) {
    return new Session(
        new ObjectId(),
        Utils.generateRandomID(),
        Utils.generateRandomID(),
        null,
        null,
        List.of(members),
        new HashMap<>(),
        new ArrayList<>(),
        SessionState.START_VOTING,
        null,
        null,
        null,
        null,
        false,
        null,
        null);
  }
}
