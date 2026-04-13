/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.diveni.backend.Utils;
import org.junit.jupiter.api.Test;

import lombok.val;

public class MemberTest {

  @Test
  public void equal_works() {
    val memberID = Utils.generateRandomID();
    val name = "John";
    val hexColor = "#ffffff";
    val avatar = AvatarAnimal.WOLF;
    String estimation = null;
    val member = new Member(memberID, name, hexColor, avatar, estimation);
    val sameMember = new Member(memberID, name, hexColor, avatar, estimation);
    val otherMember = new Member(Utils.generateRandomID(), name, hexColor, avatar, estimation);

    assertEquals(member, sameMember);
    assertNotEquals(member, otherMember);
    assertEquals(member.hashCode(), sameMember.hashCode());
  }

  @Test
  public void updateEstimation_works() {
    val vote = "5";

    val member = new Member(null, null, null, null, null);
    val result = member.updateEstimation(vote);

    assertEquals(result.getCurrentEstimation(), vote);
    assertNull(member.getCurrentEstimation());
  }

  @Test
  public void resetEstimation_works() {
    val vote = "5";
    val member = new Member(null, null, null, null, vote);

    val result = member.resetEstimation();

    assertEquals(member.getCurrentEstimation(), vote);
    assertNull(result.getCurrentEstimation());
  }

  @Test
  public void withActive_works() {
    val member = new Member("id1", "John", "#fff", AvatarAnimal.WOLF, null, true);

    val inactive = member.withActive(false);

    assertFalse(inactive.isActive());
    assertTrue(member.isActive());
    assertEquals(member.getMemberID(), inactive.getMemberID());
    assertEquals(member.getName(), inactive.getName());
  }

  @Test
  public void updateEstimation_preservesIsActive() {
    val member = new Member("id1", "John", "#fff", AvatarAnimal.WOLF, null, false);

    val updated = member.updateEstimation("5");

    assertFalse(updated.isActive());
    assertEquals("5", updated.getCurrentEstimation());
  }

  @Test
  public void resetEstimation_preservesIsActive() {
    val member = new Member("id1", "John", "#fff", AvatarAnimal.WOLF, "5", false);

    val reset = member.resetEstimation();

    assertFalse(reset.isActive());
    assertNull(reset.getCurrentEstimation());
  }

  @Test
  public void withActive_false_setsDeactivatedAt() {
    val member = new Member("id1", "John", "#fff", AvatarAnimal.WOLF, null);

    val inactive = member.withActive(false);

    assertFalse(inactive.isActive());
    assertNotNull(inactive.getDeactivatedAt());
  }

  @Test
  public void withActive_true_clearsDeactivatedAt() {
    val member = new Member("id1", "John", "#fff", AvatarAnimal.WOLF, null);
    val inactive = member.withActive(false);

    val reactivated = inactive.withActive(true);

    assertTrue(reactivated.isActive());
    assertNull(reactivated.getDeactivatedAt());
  }

  @Test
  public void updateEstimation_preservesDeactivatedAt() {
    val member = new Member("id1", "John", "#fff", AvatarAnimal.WOLF, null);
    val inactive = member.withActive(false);
    val deactivatedAt = inactive.getDeactivatedAt();

    val updated = inactive.updateEstimation("5");

    assertEquals(deactivatedAt, updated.getDeactivatedAt());
  }

  @Test
  public void resetEstimation_preservesDeactivatedAt() {
    val member = new Member("id1", "John", "#fff", AvatarAnimal.WOLF, "5");
    val inactive = member.withActive(false);
    val deactivatedAt = inactive.getDeactivatedAt();

    val reset = inactive.resetEstimation();

    assertEquals(deactivatedAt, reset.getDeactivatedAt());
  }
}
