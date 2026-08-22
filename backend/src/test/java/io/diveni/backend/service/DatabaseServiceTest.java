/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import io.diveni.backend.Utils;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionState;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.val;

@SpringBootTest
public class DatabaseServiceTest {

  @Autowired private DatabaseService databaseService;

  private Session createMinimalSession() {
    return new Session(
        new ObjectId(),
        Utils.generateRandomID(),
        Utils.generateRandomID(),
        null,
        UUID.randomUUID(),
        new ArrayList<Member>(),
        new HashMap<>(),
        new ArrayList<>(),
        SessionState.WAITING_FOR_MEMBERS,
        null,
        null,
        null,
        LocalDate.now(),
        false,
        null);
  }

  @Test
  public void saveSession_assignsVersion() {
    val session = createMinimalSession();
    val saved = databaseService.saveSession(session);
    assertNotNull(saved.getVersion(), "First save should assign a version");
  }

  @Test
  public void saveSession_incrementsVersionOnUpdate() {
    val session = createMinimalSession();
    val firstSave = databaseService.saveSession(session);
    Integer firstVersion = firstSave.getVersion();

    // Modify and save again via the returned session (has version preserved)
    val modified = firstSave.setLastModified(new java.util.Date());
    val secondSave = databaseService.saveSession(modified);
    assertNotNull(secondSave.getVersion());
    assertTrue(
        secondSave.getVersion() > firstVersion, "Version should increment on subsequent saves");
  }

  @Test
  public void saveSession_restoresVersionFromDb() {
    val session = createMinimalSession();
    val saved = databaseService.saveSession(session);
    Integer expectedVersion = saved.getVersion();

    // Create a fresh session with same databaseID but null version
    // (simulates what happens after immutable copy in mutation methods)
    val updatedMembers = new ArrayList<>(saved.getMembers());
    updatedMembers.add(new Member(Utils.generateRandomID(), "Test", null, null, null));
    Session versionlessCopy =
        new Session(
            saved.getDatabaseID(),
            saved.getSessionID(),
            saved.getAdminID(),
            saved.getSessionConfig(),
            saved.getAdminCookie(),
            updatedMembers,
            saved.getMemberVoted(),
            saved.getCurrentHighlights(),
            saved.getSessionState(),
            null,
            saved.getAccessToken(),
            saved.getTimerTimestamp(),
            saved.getCreationTime(),
            saved.getHostVoting(),
            saved.getHostEstimation());

    val restored = databaseService.saveSession(versionlessCopy);
    assertNotNull(restored.getVersion());
    assertTrue(
        restored.getVersion() > expectedVersion,
        "Version should increment even when starting from a versionless copy");
  }

  @Test
  public void deleteSession_deletesSuccessfully() {
    val session = createMinimalSession();
    val saved = databaseService.saveSession(session);

    // Delete via the saved session (has version)
    databaseService.deleteSession(saved);

    // Verify it's gone
    val deleted = databaseService.getSessionByID(saved.getSessionID());
    assertTrue(deleted.isEmpty(), "Session should be deleted");
  }

  @Test
  public void deleteSession_worksWithVersionlessCopy() {
    val session = createMinimalSession();
    val saved = databaseService.saveSession(session);

    // Create a versionless copy (simulates immutable copy pattern)
    Session versionlessCopy =
        new Session(
            saved.getDatabaseID(),
            saved.getSessionID(),
            saved.getAdminID(),
            saved.getSessionConfig(),
            saved.getAdminCookie(),
            saved.getMembers(),
            saved.getMemberVoted(),
            saved.getCurrentHighlights(),
            saved.getSessionState(),
            null,
            saved.getAccessToken(),
            saved.getTimerTimestamp(),
            saved.getCreationTime(),
            saved.getHostVoting(),
            saved.getHostEstimation());

    // Should succeed due to version restoration in deleteSession
    databaseService.deleteSession(versionlessCopy);

    val deleted = databaseService.getSessionByID(saved.getSessionID());
    assertTrue(deleted.isEmpty(), "Session should be deleted");
  }

  @Test
  public void saveSession_handlesConcurrentSave() {
    // Verify that two saves on the same session don't throw
    val session = createMinimalSession();
    val firstSave = databaseService.saveSession(session);

    // Simulate two updates via the same correctly-versioned object
    val update1 = firstSave.setLastModified(new java.util.Date());
    val result1 = databaseService.saveSession(update1);
    assertNotNull(result1.getVersion());

    val update2 = result1.setLastModified(new java.util.Date());
    val result2 = databaseService.saveSession(update2);
    assertNotNull(result2.getVersion());
    assertTrue(result2.getVersion() > result1.getVersion());
  }
}
