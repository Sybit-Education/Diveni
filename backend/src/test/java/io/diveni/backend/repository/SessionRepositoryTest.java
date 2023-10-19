/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import io.diveni.backend.Utils;
import io.diveni.backend.model.AvatarAnimal;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionState;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import lombok.val;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class SessionRepositoryTest {

  @Autowired private SessionRepository sessionRepo;

  @Test
  public void saveSession_returnsSession() {
    val adminID = Utils.generateRandomID();
    val membersID = Utils.generateRandomID();
    val session =
        new Session(
            new ObjectId(),
            adminID,
            membersID,
            null,
            null,
            new ArrayList<Member>(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            null);

    assertEquals(session, sessionRepo.save(session));
  }

  @Test
  public void addMemberToSession_addsMember() {
    val adminID = Utils.generateRandomID();
    val membersID = Utils.generateRandomID();
    val member = new Member(Utils.generateRandomID(), "John", "0x0a0a0a", AvatarAnimal.CAMEL, null);
    val members = new ArrayList<Member>();
    members.add(member);
    val session =
        new Session(
            new ObjectId(),
            adminID,
            membersID,
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

    assertEquals(session, sessionRepo.save(session));
  }
}
