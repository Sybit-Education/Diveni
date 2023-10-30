/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

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
            null);

    webSocketService.sendSessionStateToMember(session, defaultMemberPrincipal.getMemberID());

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.MEMBER_UPDATES_DESTINATION,
            session.getSessionState().toString());
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
            new AdminVote("XL"));

    webSocketService.sendMembersAdminVote(session);

    verify(simpMessagingTemplateMock, times(1))
        .convertAndSendToUser(
            defaultMemberPrincipal.getMemberID(),
            WebSocketService.ADMIN_UPDATED_ESTIMATION,
            session.getHostEstimation());
  }
}
