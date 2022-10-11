/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.principals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.diveni.backend.Utils;
import org.junit.jupiter.api.Test;

import lombok.val;

public class MemberPrincipalTest {

  @Test
  public void principal_creationWorks() {
    val sessionID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();
    val principal = new MemberPrincipal(sessionID, memberID);
    assertEquals(principal.getSessionID(), sessionID);
    assertEquals(principal.getMemberID(), memberID);
    assertEquals(principal.getName(), memberID);
  }

  @Test
  public void principal_equalsAnother() {
    val sessionID = Utils.generateRandomID();
    val memberID = Utils.generateRandomID();

    val principal = new MemberPrincipal(sessionID, memberID);
    val samePrincipal = new MemberPrincipal(sessionID, memberID);

    val otherPrincipal = new MemberPrincipal(Utils.generateRandomID(), Utils.generateRandomID());

    assertNotEquals(principal, otherPrincipal);
    assertEquals(principal, samePrincipal);
    assertEquals(principal.hashCode(), samePrincipal.hashCode());
  }
}
