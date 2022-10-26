/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import io.diveni.backend.Utils;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionState;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.model.notification.MemberPayload;
import io.diveni.backend.model.notification.Notification;
import io.diveni.backend.model.notification.NotificationType;
import io.diveni.backend.service.DatabaseService;
import io.diveni.backend.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import io.diveni.backend.principals.AdminPrincipal;
import io.diveni.backend.principals.MemberPrincipal;
import lombok.val;

@Controller
public class WebsocketController {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketController.class);
  @Autowired DatabaseService databaseService;
  @Autowired private WebSocketService webSocketService;

  @MessageMapping("/registerAdminUser")
  public void registerAdminUser(AdminPrincipal principal) {
    LOGGER.debug("--> registerAdminUser()");
    Session session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID());
    webSocketService.setAdminUser(principal);
    if (session.getTimerTimestamp() != null) {
      session = session.setTimerTimestamp(Utils.getTimestampISO8601(new Date()));
      databaseService.saveSession(session);
      if (!SessionState.VOTING_FINISHED.equals(session.getSessionState())) {
        webSocketService.sendTimerStartMessage(session, session.getTimerTimestamp());
      }
    }
    if (session.getMembers().size() > 0) {
      webSocketService.sendNotification(
          session, new Notification(NotificationType.ADMIN_JOINED, null));
    }
    LOGGER.debug("<-- registerAdminUser()");
  }

  @MessageMapping("/registerMember")
  public void joinMember(MemberPrincipal principal) {
    LOGGER.debug("--> joinMember()");
    val session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID());
    webSocketService.addMemberIfNew(principal);
    webSocketService.sendMembersUpdate(session);
    webSocketService.sendSessionStateToMember(session, principal.getName());
    if (session.getTimerTimestamp() != null) {
      webSocketService.sendTimerStartMessageToUser(
          session, session.getTimerTimestamp(), principal.getMemberID());
    }
    webSocketService.sendNotification(
        session,
        new Notification(
            NotificationType.MEMBER_JOINED, new MemberPayload(principal.getMemberID())));
    LOGGER.debug("<-- joinMember()");
  }

  @MessageMapping("/unregister")
  public void removeMember(Principal principal) {
    LOGGER.debug("--> removeMember()");
    if (principal instanceof MemberPrincipal) {
      webSocketService.removeMember((MemberPrincipal) principal);
      val session =
          ControllerUtils.getSessionByMemberIDOrThrowResponse(
                  databaseService, ((MemberPrincipal) principal).getMemberID())
              .removeMember(((MemberPrincipal) principal).getMemberID());
      databaseService.saveSession(session);
      webSocketService.sendMembersUpdate(session);
      webSocketService.sendNotification(
          session,
          new Notification(
              NotificationType.MEMBER_LEFT,
              new MemberPayload(((MemberPrincipal) principal).getMemberID())));
    } else {
      val session =
          ControllerUtils.getSessionOrThrowResponse(
              databaseService, ((AdminPrincipal) principal).getSessionID());
      webSocketService.sendNotification(
          session, new Notification(NotificationType.ADMIN_LEFT, null));
      webSocketService.removeAdmin((AdminPrincipal) principal);
      LOGGER.debug("<-- removeMember()");
    }
  }

  @MessageMapping("/kick-member")
  public void kickMember(AdminPrincipal principal, @Payload String memberID) {
    val session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID())
            .removeMember(memberID);
    databaseService.saveSession(session);
    webSocketService.sendMembersUpdate(session);
    webSocketService.sendNotification(
        session, new Notification(NotificationType.MEMBER_LEFT, new MemberPayload(memberID)));
    webSocketService.removeMember(new MemberPrincipal(principal.getSessionID(), memberID));
  }

  @MessageMapping("/closeSession")
  public void closeSession(AdminPrincipal principal) {
    LOGGER.debug("--> closeSession()");
    val session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID());
    webSocketService.sendSessionStateToMembers(
        session.updateSessionState(SessionState.SESSION_CLOSED));
    webSocketService.removeSession(session);
    databaseService.deleteSession(session);
    LOGGER.debug("<-- closeSession()");
  }

  @MessageMapping("/memberUpdate")
  public void getMemberUpdate(AdminPrincipal principal) {
    LOGGER.debug("--> getMemberUpdate()");
    val session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID());
    webSocketService.sendMembersUpdate(session);
    LOGGER.debug("<-- getMemberUpdate()");
  }

  @MessageMapping("/startVoting")
  public void startEstimation(AdminPrincipal principal) {
    LOGGER.debug("--> startEstimation()");
    val session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID())
            .updateSessionState(SessionState.START_VOTING)
            .resetCurrentHighlights()
            .setTimerTimestamp(Utils.getTimestampISO8601(new Date()));
    databaseService.saveSession(session);
    webSocketService.sendSessionStateToMembers(session);
    webSocketService.sendTimerStartMessage(session, session.getTimerTimestamp());
    LOGGER.debug("<-- startEstimation()");
  }

  @MessageMapping("/votingFinished")
  public void votingFinished(AdminPrincipal principal) {
    LOGGER.debug("--> votingFinished()");
    val session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID())
            .updateSessionState(SessionState.VOTING_FINISHED)
            .selectHighlightedMembers()
            .resetTimerTimestamp();
    databaseService.saveSession(session);
    webSocketService.sendMembersUpdate(session);
    webSocketService.sendSessionStateToMembers(session);
    LOGGER.debug("<-- votingFinished()");
  }

  @MessageMapping("/vote")
  public synchronized void processVote(@Payload String vote, MemberPrincipal member) {
    LOGGER.debug("--> processVote()");
    val session =
        ControllerUtils.getSessionByMemberIDOrThrowResponse(databaseService, member.getMemberID())
            .updateEstimation(member.getMemberID(), vote);
    webSocketService.sendMembersUpdate(session);
    databaseService.saveSession(session);

    boolean votingCompleted = checkIfAllMembersVoted(session.getMembers());
    if (votingCompleted) {
      votingFinished(
          new AdminPrincipal(
              member.getSessionID(),
              databaseService.getSessionByID(member.getSessionID()).get().getAdminID()));
    }
    LOGGER.debug("<-- processVote()");
  }

  private boolean checkIfAllMembersVoted(List<Member> members) {
    return members.stream().filter(m -> m.getCurrentEstimation() == null).count() == 0;
  }

  @MessageMapping("/restart")
  public synchronized void restartVote(AdminPrincipal principal) {
    LOGGER.debug("--> restartVote()");
    val session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID())
            .updateSessionState(SessionState.START_VOTING)
            .resetEstimations()
            .setTimerTimestamp(Utils.getTimestampISO8601(new Date()));
    databaseService.saveSession(session);
    webSocketService.sendMembersUpdate(session);
    webSocketService.sendSessionStateToMembers(session);
    webSocketService.sendTimerStartMessage(session, session.getTimerTimestamp());
    LOGGER.debug("<-- restartVote()");
  }

  @MessageMapping("/adminUpdatedUserStories")
  public synchronized void adminUpdatedUserStories(
      AdminPrincipal principal, @Payload List<UserStory> userStories) {
    LOGGER.debug("--> adminUpdatedUserStories()");
    val session =
        ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID())
            .updateUserStories(userStories);
    databaseService.saveSession(session);
    webSocketService.sendUpdatedUserStoriesToMembers(session);
    LOGGER.debug("<-- adminUpdatedUserStories()");
  }
}
