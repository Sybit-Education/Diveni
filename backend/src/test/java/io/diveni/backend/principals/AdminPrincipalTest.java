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

public class AdminPrincipalTest {

  @Test
  public void principal_creationWorks() {
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();
    val principal = new AdminPrincipal(sessionID, adminID);
    assertEquals(principal.getSessionID(), sessionID);
    assertEquals(principal.getAdminID(), adminID);
    assertEquals(principal.getName(), adminID);
  }

  @Test
  public void principal_equalsAnother() {
    val sessionID = Utils.generateRandomID();
    val adminID = Utils.generateRandomID();

    val principal = new AdminPrincipal(sessionID, adminID);
    val samePrincipal = new AdminPrincipal(sessionID, adminID);

    val otherPrincipal = new AdminPrincipal(Utils.generateRandomID(), Utils.generateRandomID());

    assertNotEquals(principal, otherPrincipal);
    assertEquals(principal, samePrincipal);
    assertEquals(principal.hashCode(), samePrincipal.hashCode());
  }
}
