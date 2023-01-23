/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    val jobTitle = "Frontend";
    String estimation = null;
    val member = new Member(memberID, name, hexColor, avatar, estimation, jobTitle);
    val sameMember = new Member(memberID, name, hexColor, avatar, estimation, jobTitle);
    val otherMember = new Member(Utils.generateRandomID(), name, hexColor, avatar, estimation, jobTitle);

    assertEquals(member, sameMember);
    assertNotEquals(member, otherMember);
    assertEquals(member.hashCode(), sameMember.hashCode());
  }

  @Test
  public void updateEstimation_works() {
    val vote = "5";

    val member = new Member(null, null, null, null, null,null);
    val result = member.updateEstimation(vote);

    assertEquals(result.getCurrentEstimation(), vote);
    assertNull(member.getCurrentEstimation());
  }

  @Test
  public void resetEstimation_works() {
    val vote = "5";
    val member = new Member(null, null, null, null, vote, null);

    val result = member.resetEstimation();

    assertEquals(member.getCurrentEstimation(), vote);
    assertNull(result.getCurrentEstimation());
  }
}
