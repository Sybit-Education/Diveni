/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.diveni.backend.model.Member;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionConfig;
import io.diveni.backend.model.SessionState;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.model.notification.Notification;
import io.diveni.backend.model.notification.NotificationType;
import io.diveni.backend.principals.AdminPrincipal;
import io.diveni.backend.principals.MemberPrincipal;
import io.diveni.backend.service.DatabaseService;
import io.diveni.backend.service.WebSocketService;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pure unit tests for {@link WebsocketController} focused on branch-level logic that the
 * SpringBootTest-based integration test is too heavyweight to exercise. Uses Mockito + reflection
 * to bypass field injection.
 */
public class WebsocketControllerUnitTest {

  private WebsocketController controller;
  private DatabaseService databaseService;
  private WebSocketService webSocketService;

  @BeforeEach
  void setUp() throws Exception {
    controller = new WebsocketController();
    databaseService = mock(DatabaseService.class);
    webSocketService = mock(WebSocketService.class);
    injectField(controller, "databaseService", databaseService);
    injectField(controller, "webSocketService", webSocketService);
  }

  private static void injectField(Object target, String fieldName, Object value) throws Exception {
    Field f = WebsocketController.class.getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(target, value);
  }

  private Session sessionInState(SessionState state, List<Member> members) {
    return new Session(
        null,
        "sess-1",
        "admin-1",
        null,
        null,
        members,
        new HashMap<>(),
        new ArrayList<>(),
        state,
        null,
        null,
        null,
        null,
        false,
        null,
        null);
  }

  private Session sessionWithStoriesAndIndex(List<UserStory> stories, Integer index) {
    SessionConfig config = new SessionConfig(List.of("1", "2", "3"), stories, 60, "USER_STORY", null);
    return new Session(
        null,
        "sess-1",
        "admin-1",
        config,
        null,
        List.of(),
        new HashMap<>(),
        new ArrayList<>(),
        SessionState.WAITING_FOR_MEMBERS,
        null,
        null,
        null,
        null,
        false,
        null,
        index);
  }

  private static UserStory story(String id) {
    return new UserStory(id, "title-" + id, "desc", null, true, null);
  }

  @Test
  void checkIfAllMembersVoted_returnsFalse_forEmptyMemberList() throws Exception {
    Session session = mock(Session.class);
    when(session.getHostVoting()).thenReturn(false);

    Method m =
        WebsocketController.class.getDeclaredMethod(
            "checkIfAllMembersVoted", List.class, Session.class);
    m.setAccessible(true);
    boolean result = (boolean) m.invoke(controller, List.of(), session);

    assertFalse(result, "empty active-member list must never be considered 'all voted'");
  }

  @Test
  void deactivateMember_isNoOp_whenSessionAlreadyGone() {
    // Session disappears before deactivateMember runs (closeSession race). We must
    // short-circuit BEFORE touching the in-memory principal list — otherwise
    // removeMemberPrincipal's getSessionPrincipals throws ResponseStatusException
    // because the principal-list entry was cleared by removeSession a moment earlier.
    MemberPrincipal principal = new MemberPrincipal("sess-gone", "m-1");
    when(databaseService.getSessionByMemberID("m-1")).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> controller.deactivateMember(principal));

    verify(webSocketService, never()).removeMemberPrincipal(any());
    verify(databaseService, never()).saveSession(any());
    verify(webSocketService, never()).sendMembersUpdate(any());
  }

  @Test
  void deactivateMember_doesNotTriggerVotingFinished_outsideStartVoting() {
    // Two members, both with votes; one goes inactive, the other is still active
    // and voted — so checkIfAllMembersVoted returns true. Only the state guard
    // keeps votingFinished from firing.
    Member leaving = new Member("m-1", "Alice", "#fff", null, "5");
    Member staying = new Member("m-2", "Bob", "#000", null, "8");
    Session session =
        sessionInState(SessionState.WAITING_FOR_MEMBERS, List.of(leaving, staying));

    when(databaseService.getSessionByMemberID("m-1")).thenReturn(Optional.of(session));
    when(databaseService.saveSession(any(Session.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    controller.deactivateMember(new MemberPrincipal(session.getSessionID(), "m-1"));

    verify(webSocketService).sendMembersUpdate(any(Session.class));
    // votingFinished would call sendSessionStateToMembers; assert it did not
    verify(webSocketService, never()).sendSessionStateToMembers(any());
  }

  @Test
  void adminUpdatedUserStories_broadcastsSanitizedIndex_whenIndexBecomesInvalid() {
    UserStory a = story("a");
    UserStory b = story("b");
    Session before = sessionWithStoriesAndIndex(List.of(a, b), 1);
    AdminPrincipal admin = new AdminPrincipal(before.getSessionID(), before.getAdminID());

    when(databaseService.getSessionByID(before.getSessionID())).thenReturn(Optional.of(before));
    when(databaseService.saveSession(any(Session.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    controller.adminUpdatedUserStories(admin, List.of(a));

    verify(webSocketService).sendUpdatedUserStoriesToMembers(any(Session.class));
    verify(webSocketService).sendSelectedUserStoryToMembers(any(Session.class), isNull());
  }

  @Test
  void joinMember_activeMemberBranch_broadcastsMembersUpdate() {
    Member alreadyActive = new Member("m-1", "Alice", "#fff", null, null);
    Session session = sessionInState(SessionState.START_VOTING, List.of(alreadyActive));
    MemberPrincipal principal = new MemberPrincipal(session.getSessionID(), "m-1");

    when(databaseService.getSessionByID(session.getSessionID()))
        .thenReturn(Optional.of(session));

    controller.joinMember(principal);

    verify(webSocketService).addMemberIfNew(principal);
    verify(webSocketService).sendMembersUpdate(session);
    // no MEMBER_JOINED on active rejoin — nothing changed state-wise
    verify(webSocketService, never())
        .sendNotification(
            any(Session.class),
            argThat((Notification n) -> n != null && n.getType() == NotificationType.MEMBER_JOINED));
  }

  @Test
  void joinMember_reactivationBranch_doesNotBroadcastMemberJoined() {
    // Grace-period reconnect: the matching deactivateMember path is silent
    // (no MEMBER_LEFT broadcast), so reactivation must be silent too —
    // otherwise flaky Wi-Fi produces an unbalanced stream of 'joined' toasts.
    Member inactive = new Member("m-1", "Alice", "#fff", null, "5", false, java.time.Instant.now());
    Session session =
        sessionInState(SessionState.WAITING_FOR_MEMBERS, List.of(inactive));
    MemberPrincipal principal = new MemberPrincipal(session.getSessionID(), "m-1");

    when(databaseService.getSessionByID(session.getSessionID()))
        .thenReturn(Optional.of(session));
    when(databaseService.saveSession(any(Session.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    controller.joinMember(principal);

    verify(webSocketService).sendMembersUpdate(any(Session.class));
    verify(webSocketService, never())
        .sendNotification(
            any(Session.class),
            argThat((Notification n) -> n != null && n.getType() == NotificationType.MEMBER_JOINED));
  }

  @Test
  void unregisterAdmin_doesNotSendAdminLeft_whenRemoveAdminReturnsFalse() {
    AdminPrincipal stale = new AdminPrincipal("sess-1", "admin-1");
    Session session = sessionInState(SessionState.WAITING_FOR_MEMBERS, List.of());

    when(databaseService.getSessionByID("sess-1")).thenReturn(Optional.of(session));
    when(webSocketService.removeAdmin(stale)).thenReturn(false);

    controller.removeMember(stale);

    verify(webSocketService).markPendingAdminUnregister("admin-1");
    verify(webSocketService).removeAdmin(stale);
    verify(webSocketService, never())
        .sendNotification(
            any(Session.class),
            argThat((Notification n) -> n != null && n.getType() == NotificationType.ADMIN_LEFT));
  }

  @Test
  void unregisterAdmin_sendsAdminLeft_whenRemoveAdminReturnsTrue() {
    AdminPrincipal current = new AdminPrincipal("sess-1", "admin-1");
    Session session = sessionInState(SessionState.WAITING_FOR_MEMBERS, List.of());

    when(databaseService.getSessionByID("sess-1")).thenReturn(Optional.of(session));
    when(webSocketService.removeAdmin(current)).thenReturn(true);

    controller.removeMember(current);

    verify(webSocketService)
        .sendNotification(
            eq(session),
            argThat((Notification n) -> n != null && n.getType() == NotificationType.ADMIN_LEFT));
  }

  @Test
  void adminUpdatedUserStories_doesNotRebroadcastIndex_whenStillValid() {
    UserStory a = story("a");
    UserStory b = story("b");
    Session before = sessionWithStoriesAndIndex(List.of(a, b), 0);
    AdminPrincipal admin = new AdminPrincipal(before.getSessionID(), before.getAdminID());

    when(databaseService.getSessionByID(before.getSessionID())).thenReturn(Optional.of(before));
    when(databaseService.saveSession(any(Session.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    controller.adminUpdatedUserStories(admin, List.of(a, b));

    verify(webSocketService).sendUpdatedUserStoriesToMembers(any(Session.class));
    verify(webSocketService, never()).sendSelectedUserStoryToMembers(any(), any());
  }
}
