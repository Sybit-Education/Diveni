/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.diveni.backend.Utils;
import io.diveni.backend.model.AdminVote;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.MemberUpdate;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionConfig;
import io.diveni.backend.model.SessionState;
import io.diveni.backend.model.notification.Notification;
import io.diveni.backend.model.notification.NotificationType;
import io.diveni.backend.principals.AdminPrincipal;
import io.diveni.backend.principals.MemberPrincipal;
import io.diveni.backend.principals.SessionPrincipals;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import lombok.val;

public class WebSocketServiceTest {

  @Mock SimpMessagingTemplate simpMessagingTemplateMock;

  @InjectMocks private WebSocketService webSocketService;

  @Mock private DatabaseService databaseService;

  private final AdminPrincipal defaultAdminPrincipal =
      new AdminPrincipal(Utils.generateRandomID(), Utils.generateRandomID());

  private final MemberPrincipal defaultMemberPrincipal =
      new MemberPrincipal(defaultAdminPrincipal.getSessionID(), Utils.generateRandomID());

  @BeforeEach
  public void initEach() {
    MockitoAnnotations.openMocks(this);
  }

  void setDefaultAdminPrincipal(Set<MemberPrincipal> members) throws Exception {
    val sessionPrincipalsField = WebSocketService.class.getDeclaredField("sessionPrincipalList");
    sessionPrincipalsField.setAccessible(true);
    val sessionPrincipals =
        List.of(
            new SessionPrincipals(
                defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal, members));
    sessionPrincipalsField.set(webSocketService, sessionPrincipals);
  }

  @Test
  public void setAdmin_isAdded() throws Exception {
    val adminPrincipal = new AdminPrincipal(Utils.generateRandomID(), Utils.generateRandomID());

    webSocketService.setAdminUser(adminPrincipal);

    assertTrue(
        webSocketService.getSessionPrincipalList().stream()
            .anyMatch(p -> p.adminPrincipal() == adminPrincipal));
  }

  @Test
  public void setExistingAdmin_isOverwritten() throws Exception {
    setDefaultAdminPrincipal(new HashSet<>());
    val adminPrincipal =
        new AdminPrincipal(
            defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal.getAdminID());

    webSocketService.setAdminUser(adminPrincipal);

    assertTrue(
        webSocketService.getSessionPrincipalList().stream()
            .anyMatch(p -> p.adminPrincipal() == adminPrincipal));
  }

  @Test
  public void getSessionPrincipals_isCorrect() throws Exception {
    setDefaultAdminPrincipal(new HashSet<>());

    val sessionPrincipals =
        webSocketService.getSessionPrincipals(defaultAdminPrincipal.getSessionID());

    Assertions.assertEquals(
        new SessionPrincipals(
            defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal, Set.of()),
        sessionPrincipals);
  }

  @Test
  public void getMissingSessionEntry_isError() throws Exception {
    assertThrows(
        ResponseStatusException.class,
        () -> webSocketService.getSessionPrincipals(defaultAdminPrincipal.getSessionID()));
  }

  @Test
  public void addMember_isAdded() throws Exception {
    setDefaultAdminPrincipal(new HashSet<>());
    val memberPrincipal =
        new MemberPrincipal(defaultAdminPrincipal.getSessionID(), Utils.generateRandomID());

    webSocketService.addMemberIfNew(memberPrincipal);

    Assertions.assertTrue(
        webSocketService
            .getSessionPrincipals(defaultAdminPrincipal.getSessionID())
            .memberPrincipals()
            .contains(memberPrincipal));
  }

  @Test
  public void addExistingMember_notDuplicate() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));

    webSocketService.addMemberIfNew(defaultMemberPrincipal);

    Assertions.assertEquals(
        1,
        webSocketService
            .getSessionPrincipals(defaultAdminPrincipal.getSessionID())
            .memberPrincipals()
            .size());
  }

  @Test
  public void removeMember_isRemoved() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));

    webSocketService.removeMember(defaultMemberPrincipal);

    Assertions.assertTrue(
        webSocketService
            .getSessionPrincipals(defaultAdminPrincipal.getSessionID())
            .memberPrincipals()
            .isEmpty());
  }

  @Test
  public void sendMembersUpdate_sendsUpdate() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendMembersUpdate(session);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultAdminPrincipal.getName(),
            WebSocketService.MEMBERS_UPDATED_DESTINATION,
            new MemberUpdate(session.getMembers(), session.getCurrentHighlights()));
  }

  @Test
  public void sendSessionState_sendsState() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendSessionStateToMember(session, defaultMemberPrincipal.getMemberID());

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION,
            session.getSessionState().toString());
  }

  @Test
  public void sendSessionStateWithAutoRevealAndTrue_sendsState() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendSessionStateToMemberWithAutoReveal(
        session, defaultMemberPrincipal.getMemberID(), true);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION_AUTOREVEAL,
            session.getSessionState().toString() + " true");
  }

  @Test
  public void sendSessionStateWithAutoRevealAndFalse_sendsState() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendSessionStateToMemberWithAutoReveal(
        session, defaultMemberPrincipal.getMemberID(), false);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION_AUTOREVEAL,
            session.getSessionState().toString() + " false");
  }

  @Test
  public void sendSessionStates_sendsToAll() throws Exception {
    val memberPrincipal =
        new MemberPrincipal(defaultAdminPrincipal.getSessionID(), Utils.generateRandomID());
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(
                new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                new Member(memberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendSessionStateToMembers(session);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION,
            session.getSessionState().toString());
    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            memberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION,
            session.getSessionState().toString());
  }

  @Test
  public void sendSessionStatesAutoRevealAndTrue_sendsToAll() throws Exception {
    val memberPrincipal =
        new MemberPrincipal(defaultAdminPrincipal.getSessionID(), Utils.generateRandomID());
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(
                new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                new Member(memberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendSessionStateToMembersWithAutoReveal(session, true);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION_AUTOREVEAL,
            session.getSessionState().toString() + " true");
    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            memberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION_AUTOREVEAL,
            session.getSessionState().toString() + " true");
  }

  @Test
  public void sendSessionStatesAutoRevealAndFalse_sendsToAll() throws Exception {
    val memberPrincipal =
        new MemberPrincipal(defaultAdminPrincipal.getSessionID(), Utils.generateRandomID());
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(
                new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                new Member(memberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendSessionStateToMembersWithAutoReveal(session, false);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION_AUTOREVEAL,
            session.getSessionState().toString() + " false");
    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            memberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION_AUTOREVEAL,
            session.getSessionState().toString() + " false");
  }

  @Test
  public void sendUpdatedUserStoriesToMembers_sendsToAll() throws Exception {
    val memberPrincipal =
        new MemberPrincipal(defaultAdminPrincipal.getSessionID(), Utils.generateRandomID());
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            new SessionConfig(List.of(), List.of(), null, "US_MANUALLY", "password"),
            null,
            List.of(
                new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                new Member(memberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendUpdatedUserStoriesToMembers(session);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.US_UPDATES_DESTINATION,
            session.getSessionConfig().getUserStories());
    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.US_UPDATES_DESTINATION,
            session.getSessionConfig().getUserStories());
  }

  @Test
  public void sendSelectedUserStoryToMembers_sendsToAll() throws Exception {
    Integer selectedUserStoryIndex = 42;

    val memberPrincipal =
        new MemberPrincipal(defaultAdminPrincipal.getSessionID(), Utils.generateRandomID());
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            new SessionConfig(List.of(), List.of(), null, "US_MANUALLY", "password"),
            null,
            List.of(
                new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                new Member(memberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendSelectedUserStoryToMembers(session, selectedUserStoryIndex);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.USER_STORY_SELECTED_DESTINATION,
            selectedUserStoryIndex);
  }

  @Test
  public void adminLeft_sendsNotification() throws Exception {
    val memberPrincipal =
        new MemberPrincipal(defaultAdminPrincipal.getSessionID(), Utils.generateRandomID());
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            new SessionConfig(List.of(), List.of(), null, "US_MANUALLY", "password"),
            null,
            List.of(
                new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                new Member(memberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);
    val notification = new Notification(NotificationType.ADMIN_LEFT, null);

    webSocketService.sendNotification(session, notification);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.NOTIFICATIONS_DESTINATION,
            notification);
  }

  @Test
  public void removeSession_isRemoved() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.removeSession(session);

    assertTrue(webSocketService.getSessionPrincipalList().isEmpty());
  }

  @Test
  public void sendMembersHostVoting_sendsHostVotingUpdate() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            null);

    webSocketService.sendMembersHostVoting(session);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_HOSTVOTING,
            session.getHostVoting());
  }

  @Test
  public void sendMembersHostEstimation_sendsHostEstimationUpdate() throws Exception {
    setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
    val session =
        new Session(
            new ObjectId(),
            defaultAdminPrincipal.getSessionID(),
            defaultAdminPrincipal.getAdminID(),
            null,
            null,
            List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            null,
            null,
            false,
            new AdminVote("XL"),
            null);

    webSocketService.sendMembersAdminVote(session);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.ADMIN_UPDATED_ESTIMATION,
            session.getHostEstimation());
  }

  @Test
  public void addMember_noSessionEntry_createsEntry() {
    val sessionID = Utils.generateRandomID();
    val memberPrincipal = new MemberPrincipal(sessionID, Utils.generateRandomID());

    webSocketService.addMemberIfNew(memberPrincipal);

    val principals = webSocketService.getSessionPrincipals(sessionID);
    Assertions.assertNull(principals.adminPrincipal());
    Assertions.assertTrue(principals.memberPrincipals().contains(memberPrincipal));
  }

  @Test
  public void addMemberBeforeAdmin_adminPreservesMember() {
    val sessionID = Utils.generateRandomID();
    val memberPrincipal = new MemberPrincipal(sessionID, Utils.generateRandomID());
    val adminPrincipal = new AdminPrincipal(sessionID, Utils.generateRandomID());

    webSocketService.addMemberIfNew(memberPrincipal);
    webSocketService.setAdminUser(adminPrincipal);

    val principals = webSocketService.getSessionPrincipals(sessionID);
    Assertions.assertEquals(adminPrincipal, principals.adminPrincipal());
    Assertions.assertTrue(principals.memberPrincipals().contains(memberPrincipal));
  }

  @Test
  public void markPendingUnregister_and_consumePending_works() {
    val memberID = "member-123";

    webSocketService.markPendingUnregister(memberID);

    assertTrue(webSocketService.consumePendingUnregister(memberID));
    assertFalse(webSocketService.consumePendingUnregister(memberID));
  }

  @Test
  public void consumePendingUnregister_returnsFalse_whenNotMarked() {
    assertFalse(webSocketService.consumePendingUnregister("unknown-member"));
  }

  @Test
  public void markPendingAdminUnregister_and_consume_works() {
    val adminID = "admin-123";

    webSocketService.markPendingAdminUnregister(adminID);

    assertTrue(webSocketService.consumePendingAdminUnregister(adminID));
    assertFalse(webSocketService.consumePendingAdminUnregister(adminID));
  }

  @Test
  public void consumePendingAdminUnregister_returnsFalse_whenNotMarked() {
    assertFalse(webSocketService.consumePendingAdminUnregister("unknown-admin"));
  }

  @Test
  public void sendErrorToMember_sendsError() throws Exception {
    val memberID = "member-456";

    webSocketService.sendErrorToMember(memberID, "SESSION_NOT_FOUND");

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(memberID, WebSocketService.ERROR_DESTINATION, "SESSION_NOT_FOUND");
  }

  @Test
  public void removeAdmin_returnsTrue_whenStoredPrincipalIsCurrent() throws Exception {
    setDefaultAdminPrincipal(new HashSet<>());

    boolean removed = webSocketService.removeAdmin(defaultAdminPrincipal);

    assertTrue(removed);
    assertTrue(
        webSocketService.getSessionPrincipalList().stream()
            .allMatch(p -> p.adminPrincipal() == null));
  }

  @Test
  public void removeAdmin_returnsFalse_whenStoredPrincipalIsDifferentInstance() throws Exception {
    setDefaultAdminPrincipal(new HashSet<>());
    val staleAdmin =
        new AdminPrincipal(
            defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal.getAdminID());

    boolean removed = webSocketService.removeAdmin(staleAdmin);

    assertFalse(removed);
    assertTrue(
        webSocketService.getSessionPrincipalList().stream()
            .anyMatch(p -> p.adminPrincipal() == defaultAdminPrincipal));
  }

  @Test
  public void removeAdmin_returnsFalse_whenNoAdminStored() throws Exception {
    boolean removed = webSocketService.removeAdmin(defaultAdminPrincipal);

    assertFalse(removed);
  }
}
