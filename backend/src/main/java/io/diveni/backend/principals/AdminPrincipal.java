/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.principals;

import java.security.Principal;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class AdminPrincipal implements Principal {

  private final String sessionID;

  private final String adminID;

  public AdminPrincipal(String sessionID, String adminID) {
    this.adminID = adminID;
    this.sessionID = sessionID;
  }

  @Override
  public String getName() {
    return adminID;
  }
}
