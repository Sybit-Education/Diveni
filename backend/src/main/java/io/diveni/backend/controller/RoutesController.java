/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.diveni.backend.Utils;
import io.diveni.backend.model.DiveniData;
import io.diveni.backend.model.JoinInfo;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionConfig;
import io.diveni.backend.model.SessionState;
import io.diveni.backend.service.DatabaseService;
import io.diveni.backend.service.projectmanagementproviders.jiraserver.JiraServerService;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.val;

@RestController
public class RoutesController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutesController.class);

  @Autowired JiraServerService jiraServerService;

  @Autowired DatabaseService databaseService;

  @PostMapping(value = "/sessions")
  public ResponseEntity<Map<String, Object>> createSession(
      @RequestParam("tokenIdentifier") Optional<String> tokenIdentifier,
      @RequestBody SessionConfig sessionConfig) {
    LOGGER.debug("--> createSession(), tokenIdentifier={}", tokenIdentifier);

    val usedSessionIDs =
        databaseService.getSessions().stream()
            .map(Session::getSessionID)
            .collect(Collectors.toSet());
    val usedDatabaseIDs =
        databaseService.getSessions().stream()
            .map(Session::getDatabaseID)
            .collect(Collectors.toSet());
    ObjectId databaseID =
        Stream.generate(ObjectId::new).filter(s -> !usedDatabaseIDs.contains(s)).findFirst().get();
    List<String> sessionIds =
        Stream.generate(Utils::generateRandomID)
            .filter(s -> !usedSessionIDs.contains(s))
            .limit(2)
            .collect(Collectors.toList());
    val accessToken =
        tokenIdentifier
            .map(token -> jiraServerService.getAccessTokens().remove(token))
            .orElse(null);
    val session =
        new Session(
            databaseID,
            sessionIds.get(0),
            sessionIds.get(1),
            sessionConfig,
            UUID.randomUUID(),
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            accessToken,
            null,
            LocalDate.now()
          );
    databaseService.saveSession(session);
    val responseMap = Map.of("session", session, "adminCookie", session.getAdminCookie());

    LOGGER.debug("<-- createSession()");
    return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
  }

  @GetMapping(value = "/sessions")
  public ResponseEntity<Session> getExistingSession(@RequestParam("adminCookie") UUID adminCookie) {
    LOGGER.debug("--> getExistingSession(), adminCookie={}", adminCookie);
    val session = databaseService.getSessionByAdminCookie(adminCookie);
    if (session.isPresent()) {
      LOGGER.debug("<-- getExistingSession()");
      return new ResponseEntity<>(session.get(), HttpStatus.OK);
    } else {
      LOGGER.warn("Session with cookie was not found!");
      LOGGER.debug("<-- getExistingSession()");
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage);
    }
  }

  @GetMapping(value = "/data")
  public ResponseEntity<DiveniData> getDiveniData() {
    LOGGER.debug("--> getDiveniData()");
    int amountOfSessions = databaseService.getSessions().size() + databaseService.getDeletedSessions().size();
    int amountOfAttendees = databaseService.getSessions().stream().collect(Collectors.summingInt(s -> s.getMembers().size())); //existing Sessions
    amountOfAttendees += databaseService.getDeletedSessions().stream().collect(Collectors.summingInt(deletedSess -> deletedSess.getMembers().size()));
    LOGGER.debug("<-- getDiveniData()");
    return new ResponseEntity<DiveniData>(new DiveniData(amountOfAttendees, amountOfSessions), HttpStatus.OK);
  }

  @GetMapping(value = "/data/lastMonth")
  public ResponseEntity<DiveniData> getDiveniDataFromLastMonth() {
    LOGGER.debug("--> getDiveniDataFromLastMonth()");
    int lastMonth = LocalDate.now().getMonthValue();
    int counter = 0;
    if (lastMonth == 1) {
      counter = 11;
    } else {
      counter = -1;
    }
    int lastMonth2 = lastMonth + counter;
    int year = LocalDate.now().getYear();
    LOGGER.debug("letzter Monat: " + lastMonth2 + " Jahr: " + year);
    List<Session> sessionFromLastMonth = databaseService.getSessions()
    .stream()
    .filter(s -> s.getCreationTime().getMonthValue() == lastMonth2 && s.getCreationTime().getYear() == year)
    .collect(Collectors.toList());

    List<Session> deletedsessionFromLastMonth = databaseService.getDeletedSessions()
    .stream()
    .filter(s -> s.getCreationTime().getMonthValue() == lastMonth2 && s.getCreationTime().getYear() == year)
    .collect(Collectors.toList());

    int amountOfSessions = sessionFromLastMonth.size() + deletedsessionFromLastMonth.size();
    int amountOfAttendees = sessionFromLastMonth
    .stream()
    .collect(Collectors.summingInt(s -> s.getMembers().size()));

    amountOfAttendees += deletedsessionFromLastMonth
    .stream()
    .collect(Collectors.summingInt(s -> s.getMembers().size()));
    LOGGER.debug("<-- getDiveniDataFromLastMont()");
    return new ResponseEntity<DiveniData>(new DiveniData(amountOfAttendees, amountOfSessions), HttpStatus.OK);
  }

  @GetMapping(value = "/data/current")
  public ResponseEntity<DiveniData> getCurrentDiveniData() {
    LOGGER.debug("--> getCurrentDiveniData()");
    int amountOfSessions = databaseService.getSessions().size();
    int amountOfAttendees = databaseService.getSessions().stream().collect(Collectors.summingInt(s -> s.getMembers().size())); //existing Sessions
    LOGGER.debug("<-- getCurrentDiveniData()");
    return new ResponseEntity<DiveniData>(new DiveniData(amountOfAttendees, amountOfSessions), HttpStatus.OK);
  }

  @PostMapping(value = "/sessions/{sessionID}/join")
  public ResponseEntity<SessionConfig> joinSession(
      @PathVariable String sessionID, @RequestBody JoinInfo joinInfo) {
    LOGGER.debug("--> joinSession(), sessionID={}", sessionID);
    val session = addMemberToSession(sessionID, joinInfo.getMember(), joinInfo.getPassword());
    LOGGER.debug("<-- joinSession()");
    return new ResponseEntity<SessionConfig>(session.getSessionConfig(), HttpStatus.OK);
  }

  @GetMapping(value = "/sessions/{sessionID}")
  public @ResponseBody Session getSession(@PathVariable String sessionID) {
    LOGGER.debug("--> getSession(), sessionID={}", sessionID);
    Session session = ControllerUtils.getSessionOrThrowResponse(databaseService, sessionID);
    LOGGER.debug("<-- getSession()");
    return session;
  }

  private synchronized Session addMemberToSession(
      String sessionID, Member member, Optional<String> password) {
    LOGGER.debug("--> addMemberToSession(), sessionID={}, member={}", sessionID, member);
    val session =
        databaseService
            .getSessionByID(sessionID)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage));
    List<Member> members = session.getMembers().stream().map(m -> m).collect(Collectors.toList());
    if (members.stream().anyMatch(m -> m.getMemberID().equals(member.getMemberID()))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorMessages.memberExistsErrorMessage);
    }
    if (session.getSessionConfig().getPassword() != null) {
      if (!password.isPresent()
          || !password.get().equals(session.getSessionConfig().getPassword())) {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, ErrorMessages.wrongPasswordMessage);
      }
    }
    members.add(member);
    databaseService.saveSession(session.updateMembers(members));
    LOGGER.debug("<-- addMemberToSession()");
    return session;
  }
}
