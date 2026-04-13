/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.diveni.backend.Utils;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionState;
import io.diveni.backend.principals.MemberPrincipal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InactiveMemberCleanupTaskTest {

  @Mock private DatabaseService databaseService;

  @Mock private WebSocketService webSocketService;

  @InjectMocks private InactiveMemberCleanupTask cleanupTask;

  @BeforeEach
  public void initEach() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void cleanupStaleInactiveMembers_removesExpiredMembers() {
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val activeMember = new Member(Utils.generateRandomID(), "Alice", "#fff", null, null);
    val staleMember =
        new Member(
            Utils.generateRandomID(),
            "Bob",
            "#000",
            null,
            null,
            false,
            Instant.now().minus(Duration.ofMinutes(5)));
    val session =
        new Session(
            new ObjectId(),
            sessionID,
            adminID,
            null,
            null,
            List.of(activeMember, staleMember),
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

    when(databaseService.getSessions()).thenReturn(List.of(session));

    cleanupTask.cleanupStaleInactiveMembers();

    val sessionCaptor = ArgumentCaptor.forClass(Session.class);
    verify(databaseService).saveSession(sessionCaptor.capture());
    assertEquals(1, sessionCaptor.getValue().getMembers().size());
    assertEquals(
        activeMember.getMemberID(),
        sessionCaptor.getValue().getMembers().get(0).getMemberID());

    verify(webSocketService).removeMemberPrincipal(any(MemberPrincipal.class));
    verify(webSocketService).sendMembersUpdate(any(Session.class));
  }

  @Test
  public void cleanupStaleInactiveMembers_skipsRecentInactive() {
    val activeMember = new Member(Utils.generateRandomID(), "Alice", "#fff", null, null);
    val recentInactive =
        new Member(
            Utils.generateRandomID(),
            "Bob",
            "#000",
            null,
            null,
            false,
            Instant.now().minus(Duration.ofMinutes(1)));
    val session =
        new Session(
            new ObjectId(),
            Utils.generateRandomID(),
            Utils.generateRandomID(),
            null,
            null,
            List.of(activeMember, recentInactive),
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

    when(databaseService.getSessions()).thenReturn(List.of(session));

    cleanupTask.cleanupStaleInactiveMembers();

    verify(databaseService, never()).saveSession(any());
    verify(webSocketService, never()).sendMembersUpdate(any());
  }

  @Test
  public void cleanupStaleInactiveMembers_handlesEmptySessionList() {
    when(databaseService.getSessions()).thenReturn(List.of());

    cleanupTask.cleanupStaleInactiveMembers();

    verify(databaseService, never()).saveSession(any());
  }
}
