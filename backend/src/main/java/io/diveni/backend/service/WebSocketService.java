/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.diveni.backend.model.MemberUpdate;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.principals.AdminPrincipal;
import io.diveni.backend.principals.MemberPrincipal;
import io.diveni.backend.principals.SessionPrincipals;
import lombok.Getter;
import lombok.val;

@Service
public class WebSocketService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketService.class);

  public static String MEMBERS_UPDATED_DESTINATION = "/updates/membersUpdated";

  public static String MEMBER_UPDATES_DESTINATION = "/updates/member";

  public static String MEMBER_UPDATES_DESTINATION_AUTOREVEAL = "/updates/member/autoreveal";

  public static String US_UPDATES_DESTINATION = "/updates/userStories";

  public static String MEMBER_UPDATES_HOSTVOTING = "/updates/hostVoting";

  public static String ADMIN_UPDATED_ESTIMATION = "/updates/hostEstimation";

  public static String NOTIFICATIONS_DESTINATION = "/updates/notifications";

  public static String START_TIMER_DESTINATION = "/updates/startTimer";

  public static String USER_STORY_SELECTED_DESTINATION = "/updates/userStorySelected";

  @Autowired private SimpMessagingTemplate simpMessagingTemplate;

  @Autowired private DatabaseService databaseService;

  @Getter private List<SessionPrincipals> sessionPrincipalList = List.of();

  public SessionPrincipals getSessionPrincipals(String sessionID) {
    LOGGER.debug("--> getSessionPrincipals(), sessionID={}", sessionID);
    SessionPrincipals sessionPrincipals =
        sessionPrincipalList.stream()
            .filter(s -> s.sessionID().equals(sessionID))
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage));
    LOGGER.debug("<-- getSessionPrincipals()");
    return sessionPrincipals;
  }

  public synchronized void addMemberIfNew(MemberPrincipal member) {
    LOGGER.debug("--> addMemberIfNew(), member={}", member.getMemberID());
    val sessionPrincipals = getSessionPrincipals(member.getSessionID());
    val updatedMembers =
        Stream.concat(
                sessionPrincipals.memberPrincipals().stream()
                    .filter(m -> !m.getName().equals(member.getName())),
                Stream.of(member))
            .collect(Collectors.toSet());
    sessionPrincipalList =
        sessionPrincipalList.stream()
            .map(
                p -> {
                  if (p == sessionPrincipals) {
                    return p.memberPrincipals(updatedMembers);
                  } else {
                    return p;
                  }
                })
            .collect(Collectors.toList());
    LOGGER.debug("<-- addMemberIfNew()");
  }

  public synchronized void removeMember(MemberPrincipal member) {
    LOGGER.debug("--> removeMember(), member={}", member.getMemberID());
    val sessionPrincipals = getSessionPrincipals(member.getSessionID());
    val updatedMembers =
        sessionPrincipals.memberPrincipals().stream()
            .filter(m -> !m.getMemberID().equals(member.getMemberID()))
            .collect(Collectors.toSet());
    sessionPrincipalList =
        sessionPrincipalList.stream()
            .map(
                p -> {
                  if (p == sessionPrincipals) return p.memberPrincipals(updatedMembers);
                  else return p;
                })
            .collect(Collectors.toList());
    databaseService.addRemovedMember();
    LOGGER.debug("<-- removeMember()");
  }

  public synchronized void removeAdmin(AdminPrincipal admin) {
    LOGGER.debug("--> removeAdmin(), admin={}", admin.getAdminID());
    sessionPrincipalList =
        sessionPrincipalList.stream()
            .map(
                p -> {
                  if (p.adminPrincipal() == admin) {
                    return p.adminPrincipal(null);
                  } else {
                    return p;
                  }
                })
            .collect(Collectors.toList());
    LOGGER.debug("<-- removeAdmin()");
  }

  public synchronized void setAdminUser(AdminPrincipal principal) {
    LOGGER.debug("--> setAdminUser(), principal={}", principal.getAdminID());
    if (sessionPrincipalList.stream()
        .anyMatch(p -> p.sessionID().equals(principal.getSessionID()))) {
      sessionPrincipalList =
          sessionPrincipalList.stream()
              .map(
                  p -> {
                    if (p.sessionID().equals(principal.getSessionID()))
                      return p.adminPrincipal(principal);
                    else return p;
                  })
              .collect(Collectors.toList());
    } else {
      sessionPrincipalList =
          Stream.concat(
                  sessionPrincipalList.stream(),
                  Stream.of(new SessionPrincipals(principal.getSessionID(), principal, Set.of())))
              .collect(Collectors.toList());
    }
    LOGGER.debug("<-- setAdminUser()");
  }

  public void sendMembersHostVoting(Session session) {
    LOGGER.debug("--> sendMembersHostVoting(), sessionID={}", session.getSessionID());
    getSessionPrincipals(session.getSessionID())
        .memberPrincipals()
        .forEach(member -> sendUpdatedHostVotingToMember(session, member.getMemberID()));
    LOGGER.debug("<-- sendMembersHostVoting()");
  }

  public void sendUpdatedHostVotingToMember(Session session, String memberID) {
    LOGGER.debug(
        "--> sendUpdatedHostVotingToMember(), sessionID={}, memberID={}",
        session.getSessionID(),
        memberID);
    simpMessagingTemplate.convertAndSendToUser(
        memberID, MEMBER_UPDATES_HOSTVOTING, session.getHostVoting());
    LOGGER.debug("<-- sendUpdatedHostVotingToMember()");
  }

  public void sendMembersUpdate(Session session) {
    LOGGER.debug("--> sendMembersUpdate(), sessionID={}", session.getSessionID());
    val sessionPrincipals = getSessionPrincipals(session.getSessionID());
    if (sessionPrincipals.adminPrincipal() != null) {
      simpMessagingTemplate.convertAndSendToUser(
          sessionPrincipals.adminPrincipal().getName(),
          MEMBERS_UPDATED_DESTINATION,
          new MemberUpdate(session.getMembers(), session.getCurrentHighlights()));
    } // else the admin left the session
    sendMembersUpdateToMembers(session);
    LOGGER.debug("<-- sendMembersUpdate()");
  }

  public void sendMembersUpdateToMembers(Session session) {
    LOGGER.debug("--> sendMembersUpdateToMembers(), sessionID={}", session.getSessionID());
    getSessionPrincipals(session.getSessionID())
        .memberPrincipals()
        .forEach(
            member ->
                simpMessagingTemplate.convertAndSendToUser(
                    member.getMemberID(),
                    MEMBERS_UPDATED_DESTINATION,
                    new MemberUpdate(session.getMembers(), session.getCurrentHighlights())));
    LOGGER.debug("<-- sendMembersUpdateToMembers()");
  }

  public void sendMembersAdminVote(Session session) {
    LOGGER.debug("--> sendMembersAdminVote(), sessionID={}", session.getSessionID());
    getSessionPrincipals(session.getSessionID())
        .memberPrincipals()
        .forEach(
            member ->
                simpMessagingTemplate.convertAndSendToUser(
                    member.getMemberID(), ADMIN_UPDATED_ESTIMATION, session.getHostEstimation()));
    LOGGER.debug("<-- sendMembersAdminVote()");
  }

  public void sendSessionStateToMembers(Session session) {
    LOGGER.debug("--> sendSessionStateToMembers(), sessionID={}", session.getSessionID());
    // TODO: Send highlighted with it
    getSessionPrincipals(session.getSessionID()).memberPrincipals().stream()
        .forEach(member -> sendSessionStateToMember(session, member.getMemberID()));
    LOGGER.debug("<-- sendSessionStateToMembers()");
  }

  public void sendSessionStateToMembersWithAutoReveal(Session session, boolean autoReveal) {
    LOGGER.debug(
        "--> sendSessionStateToMembersWithAutoReveal(), sessionID={}", session.getSessionID());
    // TODO: Send highlighted with it
    getSessionPrincipals(session.getSessionID()).memberPrincipals().stream()
        .forEach(
            member ->
                sendSessionStateToMemberWithAutoReveal(session, member.getMemberID(), autoReveal));
    LOGGER.debug("<-- sendSessionStateToMembersWithAutoReveal()");
  }

  public void sendUpdatedUserStoriesToMembers(Session session) {
    LOGGER.debug("--> sendUpdatedUserStoriesToMembers(), sessionID={}", session.getSessionID());
    getSessionPrincipals(session.getSessionID())
        .memberPrincipals()
        .forEach(member -> sendUpdatedUserStoriesToMember(session, member.getMemberID()));
    LOGGER.debug("<-- sendUpdatedUserStoriesToMembers()");
  }

  public void sendSessionStateToMemberWithAutoReveal(
      Session session, String memberID, boolean autoReveal) {
    LOGGER.debug(
        "--> sendSessionStateToMemberWithAutoReveal(), sessionID={}, memberID={}",
        session.getSessionID(),
        memberID);
    simpMessagingTemplate.convertAndSendToUser(
        memberID,
        MEMBER_UPDATES_DESTINATION_AUTOREVEAL,
        session.getSessionState().toString() + " " + autoReveal);
    LOGGER.debug("<-- sendSessionStateToMemberWithAutoReveal()");
  }

  public void sendSessionStateToMember(Session session, String memberID) {
    LOGGER.debug(
        "--> sendSessionStateToMember(), sessionID={}, memberID={}",
        session.getSessionID(),
        memberID);
    simpMessagingTemplate.convertAndSendToUser(
        memberID, MEMBER_UPDATES_DESTINATION, session.getSessionState().toString());
    LOGGER.debug("<-- sendSessionStateToMember()");
  }

  public void sendUpdatedUserStoriesToMember(Session session, String memberID) {
    LOGGER.debug(
        "--> sendUpdatedUserStoriesToMember(), sessionID={}, memberID={}",
        session.getSessionID(),
        memberID);
    simpMessagingTemplate.convertAndSendToUser(
        memberID, US_UPDATES_DESTINATION, session.getSessionConfig().getUserStories());
    LOGGER.debug("<-- sendUpdatedUserStoriesToMember()");
  }

  public void sendSelectedUserStoryToMembers(Session session, Integer index) {
    LOGGER.debug(
        "--> sendSelectedUserStoryToMembers(), sessionID={}, index={}",
        session.getSessionID(),
        index);
    getSessionPrincipals(session.getSessionID())
        .memberPrincipals()
        .forEach(
            member ->
                simpMessagingTemplate.convertAndSendToUser(
                    member.getMemberID(), USER_STORY_SELECTED_DESTINATION, index));
    LOGGER.debug("<-- sendSelectedUserStoryToMembers()");
  }

  public void sendTimerStartMessage(Session session, String timestamp) {
    LOGGER.debug("--> sendTimerStartMessage(), sessionID={}", session.getSessionID());
    val sessionPrincipals = getSessionPrincipals(session.getSessionID());
    if (sessionPrincipals.adminPrincipal() != null) {
      sendTimerStartMessageToUser(session, timestamp, sessionPrincipals.adminPrincipal().getName());
    } // else the admin left the session
    sessionPrincipals
        .memberPrincipals()
        .forEach(member -> sendTimerStartMessageToUser(session, timestamp, member.getMemberID()));
    LOGGER.debug("<-- sendTimerStartMessage()");
  }

  public void sendTimerStartMessageToUser(Session session, String timestamp, String userID) {
    LOGGER.debug(
        "--> sendTimerStartMessageToUser(), sessionID={}, userID={}",
        session.getSessionID(),
        userID);
    simpMessagingTemplate.convertAndSendToUser(userID, START_TIMER_DESTINATION, timestamp);
    LOGGER.debug("<-- sendTimerStartMessageToUser()");
  }

  public void sendNotification(Session session, Notification notification) {
    LOGGER.debug("--> sendNotification(), sessionID={}", session.getSessionID());
    getSessionPrincipals(session.getSessionID())
        .memberPrincipals()
        .forEach(
            member -> {
              simpMessagingTemplate.convertAndSendToUser(
                  member.getMemberID(), NOTIFICATIONS_DESTINATION, notification);
            });
    if (getSessionPrincipals(session.getSessionID()).adminPrincipal() != null) {
      simpMessagingTemplate.convertAndSendToUser(
          getSessionPrincipals(session.getSessionID()).adminPrincipal().getAdminID(),
          NOTIFICATIONS_DESTINATION,
          notification);
    }
    LOGGER.debug("<-- sendNotification()");
  }

  public void removeSession(Session session) {
    LOGGER.debug("--> removeSession(), sessionID={}", session.getSessionID());
    sessionPrincipalList =
        sessionPrincipalList.stream()
            .filter(p -> !p.sessionID().equals(session.getSessionID()))
            .collect(Collectors.toList());
    LOGGER.debug("<-- removeSession()");
  }
}
