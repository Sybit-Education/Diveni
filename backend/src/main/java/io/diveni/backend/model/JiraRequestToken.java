/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JiraRequestToken {
  private String token;
  private String url;

  public void setToken(String token) {
    this.token = token;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
