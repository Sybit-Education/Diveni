/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.diveni.backend.Utils;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.MemberUpdate;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionConfig;
import io.diveni.backend.model.SessionState;
import io.diveni.backend.principals.AdminPrincipal;
import io.diveni.backend.principals.MemberPrincipal;
import io.diveni.backend.repository.SessionRepository;
import io.diveni.backend.service.WebSocketService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import lombok.val;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebsocketControllerTest {

  private static final Integer TIMEOUT = 250;

  private static final String WS_ADMIN_PATH = "ws://localhost:%d/connect?sessionID=%s&adminID=%s";

  private static final String WS_MEMBER_PATH = "ws://localhost:%d/connect?sessionID=%s&memberID=%s";

  private static final String REGISTER_ADMIN = "/ws/registerAdminUser";

  private static final String REGISTER_MEMBER = "/ws/registerMember";

  private static final String START_VOTING = "/ws/startVoting";

  private static final String RESTART = "/ws/restart";

  private static final String VOTE = "/ws/vote";

  private static final String UNREGISTER = "/ws/unregister";

  private static final String ADMIN_MEMBER_UPDATES = "/users/updates/membersUpdated";

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired SessionRepository sessionRepo;

  @Autowired private WebSocketService webSocketService;

  @LocalServerPort private Integer port;

  private WebSocketStompClient webSocketStompClient;
  private BlockingQueue<String> blockingQueue;

  private final StompFrameHandler stompFrameHandler =
      new StompFrameHandler() {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
          return List.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
          try {
            blockingQueue.offer(objectMapper.writeValueAsString(o));
          } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to handle frame", e);
          }
        }
      };
  private final List<Transport> transports =
      Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));

  @BeforeAll
  public static void init() {
    objectMapper.configure(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature(), true);
  }

  private String getWsPath(String format, String sessionID, String principalID) {
    return String.format(format, port, sessionID, principalID);
  }

  private StompSession getAdminSession(String sessionID, String adminID) throws Exception {
    return webSocketStompClient
        .connect(
            getWsPath(WS_ADMIN_PATH, sessionID, adminID),
            new StompSessionHandlerAdapter() {
              @Override
              public void handleException(
                  StompSession session,
                  StompCommand command,
                  StompHeaders headers,
                  byte[] payload,
                  Throwable exception) {
                throw new RuntimeException("Failure in WebSocket handling", exception);
              }
            })
        .get();
  }

  private StompSession getMemberSession(String sessionID, String memberID) throws Exception {
    return webSocketStompClient
        .connect(
            getWsPath(WS_MEMBER_PATH, sessionID, memberID), new StompSessionHandlerAdapter() {})
        .get();
  }

  @BeforeEach
  public void initEach() {
    this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(transports));
    this.webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    this.blockingQueue = new LinkedBlockingDeque<>();
  }

  @AfterEach
  public void cleanUpEach() throws Exception {
    val sessionPrincipalsField = WebSocketService.class.getDeclaredField("sessionPrincipalList");
    sessionPrincipalsField.setAccessible(true);
    val sessionPrincipals = List.of();
    sessionPrincipalsField.set(webSocketService, sessionPrincipals);
    sessionRepo.deleteAll();
  }

  @Test
  public void registerAdminPrincipal_isRegistered() throws Exception {
    val dbID = new ObjectId();
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    sessionRepo.save(
        new Session(
            dbID,
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            new ArrayList<Member>(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null));
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    StompSession session = getAdminSession(sessionID, adminID);

    session.send(REGISTER_ADMIN, null);

    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    assertEquals(1, webSocketService.getSessionPrincipalList().size());

    Assertions.assertTrue(
        webSocketService.getSessionPrincipals(sessionID).adminPrincipal().equals(adminPrincipal));
  }

  @Test
  public void registerMemberPrincipal_isRegistered() throws Exception {
    val dbID = new ObjectId();
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val member = new Member(memberID, null, null, null, null);
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    sessionRepo.save(
        new Session(
            dbID,
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(member),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null));
    webSocketService.setAdminUser(adminPrincipal);
    val memberPrincipal = new MemberPrincipal(sessionID, memberID);
    StompSession session = getMemberSession(sessionID, memberID);

    session.send(REGISTER_MEMBER, null);

    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    Assertions.assertEquals(
        1, webSocketService.getSessionPrincipals(sessionID).memberPrincipals().size());
    Assertions.assertTrue(
        webSocketService
            .getSessionPrincipals(sessionID)
            .memberPrincipals()
            .contains(memberPrincipal));
  }

  @Test
  @Disabled
  public void registerMemberPrincipal_sendsMembersUpdates() throws Exception {
    val dbID = new ObjectId();
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val memberList =
        List.of(
            new Member(memberID, null, null, null, null),
            new Member(Utils.generateRandomID(), null, null, null, null));
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    sessionRepo.save(
        new Session(
            dbID,
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            memberList,
            new HashMap<>(),
            List.of("asdf", "bsdf"),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null));
    webSocketService.setAdminUser(adminPrincipal);
    StompSession session = getMemberSession(sessionID, memberID);
    StompSession adminSession = getAdminSession(sessionID, adminID);

    adminSession.subscribe(ADMIN_MEMBER_UPDATES, stompFrameHandler);
    session.send(REGISTER_MEMBER, null);

    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    MemberUpdate result = objectMapper.readValue(blockingQueue.poll(), MemberUpdate.class);
    assertEquals(result.getMembers(), memberList);
  }

  // @Test
  // public void registerMemberPrincipal_sendsSessionUpdates() throws Exception {
  // val sessionID = UUID.randomUUID();
  // val adminID = UUID.randomUUID();
  // val memberID = UUID.randomUUID();
  // val memberList = List.of(new Member(memberID, null, null, null, null));
  // val adminPrincipal = new AdminPrincipal(sessionID, adminID);
  // sessionRepo.save(new Session(sessionID, adminID, UUID.randomUUID(), new
  // SessionConfig(new ArrayList<>(), null),
  // memberList, SessionState.WAITING_FOR_MEMBERS));
  // webSocketService.setAdminUser(adminPrincipal);
  // StompSession session = getMemberSession(sessionID, memberID);

  // session.subscribe(MEMBER_UPDATES, stompFrameHandler);
  // session.send(REGISTER_MEMBER, null);

  // // Wait for server-side handling
  // TimeUnit.MILLISECONDS.sleep(TIMEOUT);

  // SessionState result = objectMapper.readValue(blockingQueue.poll(),
  // SessionState.class);
  // assertEquals(result, SessionState.WAITING_FOR_MEMBERS);
  // }

  @Test
  public void unregisterAdminPrincipal_isUnregistered() throws Exception {
    val dbID = new ObjectId();
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    sessionRepo.save(
        new Session(
            dbID,
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            new ArrayList<Member>(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null));
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    webSocketService.setAdminUser(adminPrincipal);
    StompSession session = getAdminSession(sessionID, adminID);

    session.send(UNREGISTER, null);

    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);
    Assertions.assertTrue(
        webSocketService.getSessionPrincipals(sessionID).memberPrincipals().isEmpty());
  }

  @Test
  public void unregisterMemberPrincipal_isUnregistered() throws Exception {
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val member = new Member(memberID, null, null, null, null);
    sessionRepo.save(
        new Session(
            new ObjectId(),
            adminID,
            Utils.generateRandomID(),
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(member),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null));
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    val memberPrincipal = new MemberPrincipal(sessionID, memberID);
    webSocketService.setAdminUser(adminPrincipal);
    webSocketService.addMemberIfNew(memberPrincipal);
    StompSession session = getMemberSession(sessionID, memberID);

    session.send(UNREGISTER, null);

    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    assertEquals(1, webSocketService.getSessionPrincipalList().size());
    Assertions.assertTrue(
        webSocketService.getSessionPrincipals(sessionID).memberPrincipals().isEmpty());
  }

  @Test
  public void memberLeavesWhileVoting_votingIsFinshed() throws Exception {
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val memberID2 = Utils.generateRandomID();
    val member = new Member(memberID, "TestName", null, null, "5");
    val member2 = new Member(memberID2, null, null, null, null);
    sessionRepo.save(
        new Session(
            new ObjectId(),
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(member, member2),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.START_VOTING,
            null,
            null,
            null,
            null));
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    val memberPrincipal = new MemberPrincipal(sessionID, memberID2);
    webSocketService.setAdminUser(adminPrincipal);
    StompSession session = getMemberSession(sessionID, memberID2);

    session.send(UNREGISTER, memberPrincipal);

    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    assertEquals(1, sessionRepo.findBySessionID(sessionID).getMembers().size());
    assertEquals(
        SessionState.VOTING_FINISHED, sessionRepo.findBySessionID(sessionID).getSessionState());
  }

  @Test
  public void memberLeavesWhileVotingAndIsNotTheLastMissingVoter_votingStartedState()
      throws Exception {
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val memberID2 = Utils.generateRandomID();
    val memberID3 = Utils.generateRandomID();
    val member = new Member(memberID, "TestName", null, null, "5");
    val member2 = new Member(memberID2, null, null, null, null);
    val member3 = new Member(memberID3, null, null, null, null);
    sessionRepo.save(
        new Session(
            new ObjectId(),
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(member, member2, member3),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.START_VOTING,
            null,
            null,
            null,
            null));
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    val memberPrincipal = new MemberPrincipal(sessionID, memberID2);
    webSocketService.setAdminUser(adminPrincipal);
    StompSession session = getMemberSession(sessionID, memberID2);

    session.send(UNREGISTER, memberPrincipal);

    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    assertEquals(2, sessionRepo.findBySessionID(sessionID).getMembers().size());
    assertEquals(
        SessionState.START_VOTING, sessionRepo.findBySessionID(sessionID).getSessionState());
  }

  @Test
  public void vote_setsVote() throws Exception {
    val dbID = new ObjectId();
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val member = new Member(memberID, null, null, null, null);
    val memberList = List.of(member);
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    sessionRepo.save(
        new Session(
            dbID,
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            memberList,
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null));
    webSocketService.setAdminUser(adminPrincipal);
    StompSession session = getMemberSession(sessionID, memberID);
    val vote = "5";

    session.send(VOTE, vote);

    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    val newMemmbers = sessionRepo.findBySessionID(sessionID).getMembers();
    assertEquals(newMemmbers, List.of(member.updateEstimation(vote)));
  }

  @Test
  @Disabled
  public void vote_sendsUpdate() throws Exception {
    val dbID = new ObjectId();
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val member = new Member(memberID, null, null, null, null);
    val memberList = List.of(member);
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    sessionRepo.save(
        new Session(
            dbID,
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            memberList,
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null));
    webSocketService.setAdminUser(adminPrincipal);
    StompSession session = getMemberSession(sessionID, memberID);
    StompSession adminSession = getAdminSession(sessionID, adminID);
    val vote = "5";

    adminSession.subscribe(ADMIN_MEMBER_UPDATES, stompFrameHandler);

    session.send(VOTE, vote);
    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    MemberUpdate result = objectMapper.readValue(blockingQueue.poll(), MemberUpdate.class);
    assertEquals(result.getMembers(), List.of(member.updateEstimation(vote)));
  }

  @Test
  public void startVoting_updatesState() throws Exception {
    val dbID = new ObjectId();
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val member = new Member(memberID, null, null, null, null);
    val memberList = List.of(member);
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    val oldSession =
        new Session(
            dbID,
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            memberList,
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);
    sessionRepo.save(oldSession);
    webSocketService.setAdminUser(adminPrincipal);
    StompSession adminSession = getAdminSession(sessionID, adminID);

    adminSession.send(START_VOTING, null);
    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    val newSession = sessionRepo.findBySessionID(oldSession.getSessionID());
    Assertions.assertEquals(SessionState.START_VOTING, newSession.getSessionState());
  }

  @Test
  public void restartVoting_resetsEstimations() throws Exception {
    val dbID = new ObjectId();
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val member = new Member(memberID, null, null, null, "5");
    val memberList = List.of(member);
    val adminPrincipal = new AdminPrincipal(sessionID, adminID);
    val oldSession =
        new Session(
            dbID,
            sessionID,
            adminID,
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            memberList,
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);
    sessionRepo.save(oldSession);
    webSocketService.setAdminUser(adminPrincipal);
    StompSession adminSession = getAdminSession(sessionID, adminID);

    adminSession.send(RESTART, null);
    // Wait for server-side handling
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    val newMembers = sessionRepo.findBySessionID(oldSession.getSessionID()).getMembers();
    Assertions.assertTrue(newMembers.stream().allMatch(m -> m.getCurrentEstimation() == null));
  }
}
