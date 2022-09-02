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
public class MemberPrincipal implements Principal {

  private final String sessionID;

  private final String memberID;

  public MemberPrincipal(String sessionID, String memberID) {
    this.memberID = memberID;
    this.sessionID = sessionID;
  }

  @Override
  public String getName() {
    return memberID.toString();
  }
}
