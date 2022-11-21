/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java.util.Date;

import io.diveni.backend.Utils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import lombok.val;

public class SessionTest {

  @Test
  public void equal_works() {
    val sessionIdBefore = Utils.generateRandomID();
    val adminIdBefore = Utils.generateRandomID();
    val dbId = new ObjectId();
    val session =
        new Session(
            dbId,
            sessionIdBefore,
            adminIdBefore,
            null,
            null,
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);
    val sameSession =
        new Session(
            dbId,
            sessionIdBefore,
            adminIdBefore,
            null,
            null,
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);
    val otherSession =
        new Session(
            new ObjectId(),
            sessionIdBefore,
            adminIdBefore,
            null,
            null,
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);

    assertEquals(session, sameSession);
    assertNotEquals(session, otherSession);
    assertEquals(session.hashCode(), sameSession.hashCode());
  }

  @Test
  public void updateEstimation_works() {
    val memberID1 = Utils.generateRandomID();
    val memberID2 = Utils.generateRandomID();
    val member1 = new Member(memberID1, null, null, null, null);
    val member2 = new Member(memberID2, null, null, null, null);
    val members = Arrays.asList(member1, member2);
    val vote = "5";

    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            members,
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);
    val result = session.updateEstimation(member1.getMemberID(), vote);

    val resultMember =
        result.getMembers().stream()
            .filter(m -> m.getMemberID().equals(member1.getMemberID()))
            .findFirst()
            .get();
    assertTrue(resultMember.getCurrentEstimation() != null);
    assertEquals(resultMember.getCurrentEstimation(), vote);
  }

  @Test
  public void resetEstimations_works() {
    val memberID1 = Utils.generateRandomID();
    val memberID2 = Utils.generateRandomID();
    val member1 = new Member(memberID1, null, null, null, "3");
    val member2 = new Member(memberID2, null, null, null, "5");
    val members = Arrays.asList(member1, member2);
    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            members,
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);

    val result = session.resetEstimations();

    assertTrue(result.getMembers().stream().allMatch(m -> m.getCurrentEstimation() == null));
  }

  @Test
  public void updateSessionState_works() {
    val oldSessionState = SessionState.WAITING_FOR_MEMBERS;
    val newSessionState = SessionState.START_VOTING;

    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            new ArrayList<Member>(),
            new HashMap<>(),
            new ArrayList<>(),
            oldSessionState,
            null,
            null,
            null,
            null);
    val result = session.updateSessionState(newSessionState);

    assertEquals(result.getSessionState(), newSessionState);
  }

  @Test
  public void setLastModified_works() {
    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            new ArrayList<Member>(),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            null,
            null);
    val date = new Date();

    val result = session.setLastModified(date);

    assertEquals(result.getLastModified(), date);
  }

  @Test
  public void addMember_works() {
    val memberID1 = Utils.generateRandomID();
    val member1 = new Member(memberID1, null, null, null, "3");
    val members = Arrays.asList(member1);
    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            members,
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);
    val memberID2 = Utils.generateRandomID();
    val member2 = new Member(memberID2, null, null, null, "5");

    val result = session.addMember(member2);

    assertEquals(result.getMembers().size(), 2);
    assertTrue(result.getMembers().stream().anyMatch(m -> m.getMemberID().equals(memberID2)));
  }

  @Test
  public void removeMember_works() {
    val memberID1 = Utils.generateRandomID();
    val memberID2 = Utils.generateRandomID();
    val member1 = new Member(memberID1, null, null, null, "3");
    val member2 = new Member(memberID2, null, null, null, "5");
    val members = Arrays.asList(member1, member2);
    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            members,
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);

    val result = session.removeMember(memberID1);

    assertTrue(result.getMembers().stream().noneMatch(m -> m.getMemberID().equals(memberID1)));
    assertEquals(result.getMembers().size(), 1);
  }

  @Test
  public void selectHighlightedMembers_works() {
    List<String> set = List.of("XS", "S", "M", "L", "XL");
    val member1 = new Member(Utils.generateRandomID(), null, null, null, "S");
    val member2 = new Member(Utils.generateRandomID(), null, null, null, "L");
    val member3 = new Member(Utils.generateRandomID(), null, null, null, "XS");
    val session =
        new Session(
            null,
            null,
            null,
            new SessionConfig(set, List.of(), 30, "US_MANUALLY", null),
            null,
            List.of(member1, member2, member3),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            null,
            null);

    val result = session.selectHighlightedMembers();

    val expectedHighlights = List.of(member3.getMemberID(), member2.getMemberID());
    assertEquals(expectedHighlights, result.getCurrentHighlights());
    assertEquals(1, result.getMemberVoted().get(member1.getMemberID()));
    assertEquals(0, result.getMemberVoted().get(member2.getMemberID()));
    assertEquals(0, result.getMemberVoted().get(member3.getMemberID()));
  }

  @Test
  public void selectHighlightedMembers_correctOrder() {
    List<String> set = List.of("1", "2", "3", "4", "5");
    val member1 = new Member(Utils.generateRandomID(), null, null, null, "4");
    val member2 = new Member(Utils.generateRandomID(), null, null, null, "1");
    val member3 = new Member(Utils.generateRandomID(), null, null, null, "3");
    val member4 = new Member(Utils.generateRandomID(), null, null, null, "5");
    val session =
        new Session(
            null,
            null,
            null,
            new SessionConfig(set, List.of(), 30, null, null),
            null,
            List.of(member1, member2, member3, member4),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            null,
            null);

    val result = session.selectHighlightedMembers();

    val expectedHighlights = List.of(member2.getMemberID(), member4.getMemberID());
    assertEquals(expectedHighlights, result.getCurrentHighlights());
    assertEquals(0, result.getCurrentHighlights().indexOf(member2.getMemberID()));
    assertEquals(1, result.getCurrentHighlights().indexOf(member4.getMemberID()));
  }

  @Test
  public void selectHighlightedMembersPrioritized_works() {
    List<String> set = List.of("XS", "S", "M", "L", "XL");
    val member1 = new Member(Utils.generateRandomID(), null, null, null, "L");
    val member2 = new Member(Utils.generateRandomID(), null, null, null, "L");
    val member3 = new Member(Utils.generateRandomID(), null, null, null, "XS");
    val map = new HashMap<String, Integer>();
    map.put(member1.getMemberID(), 1);
    map.put(member2.getMemberID(), 0);
    map.put(member3.getMemberID(), 0);
    val session =
        new Session(
            null,
            null,
            null,
            new SessionConfig(set, List.of(), 30, "US_MANUALLY", null),
            null,
            List.of(member1, member2, member3),
            map,
            new ArrayList<>(),
            null,
            null,
            null,
            null,
            null);

    val result = session.selectHighlightedMembers();

    val expectedHighlights = List.of(member3.getMemberID(), member1.getMemberID());
    assertEquals(expectedHighlights, result.getCurrentHighlights());
    assertEquals(0, result.getMemberVoted().get(member1.getMemberID()));
    assertEquals(1, result.getMemberVoted().get(member2.getMemberID()));
    assertEquals(0, result.getMemberVoted().get(member1.getMemberID()));
  }

  @Test
  public void resetHighlightedMembers_works() {
    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            new ArrayList<>(),
            new HashMap<>(),
            List.of("highlighted1", "highlighted2"),
            null,
            null,
            null,
            null,
            null);

    val result = session.resetCurrentHighlights();

    assertTrue(result.getCurrentHighlights().isEmpty());
  }

  @Test
  public void setTimerTimestamp_works() {
    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            null,
            null);
    val timestamp = Utils.getTimestampISO8601(new Date());

    val result = session.setTimerTimestamp(timestamp);

    assertEquals(timestamp, result.getTimerTimestamp());
  }

  @Test
  public void resetTimerTimestamp_works() {
    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            Utils.getTimestampISO8601(new Date()),
            null);

    val result = session.resetTimerTimestamp();

    assertNull(result.getTimerTimestamp());
  }

  @Test
  public void getSessionCreationTime_works() {
    val session =
        new Session(
            null,
            null,
            null,
            null,
            null,
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            null,
            null,
            null,
            Utils.getTimestampISO8601(new Date()),
            LocalDate.of(2222, 12, 3));

    assertEquals(LocalDate.of(2222, 12, 3), session.getCreationTime());
  }
}
