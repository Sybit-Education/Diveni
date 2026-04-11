/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Member {

  private String memberID;

  private String name;

  private String hexColor;

  private AvatarAnimal avatarAnimal;

  private String currentEstimation;

  @JsonProperty("isActive")
  private boolean isActive = true;

  public Member(
      String memberID,
      String name,
      String hexColor,
      AvatarAnimal avatarAnimal,
      String currentEstimation) {
    this(memberID, name, hexColor, avatarAnimal, currentEstimation, true);
  }

  public Member updateEstimation(String estimation) {
    return new Member(
        this.memberID, this.name, this.hexColor, this.avatarAnimal, estimation, this.isActive);
  }

  public Member resetEstimation() {
    return new Member(
        this.memberID, this.name, this.hexColor, this.avatarAnimal, null, this.isActive);
  }

  public Member withActive(boolean active) {
    return new Member(
        this.memberID, this.name, this.hexColor, this.avatarAnimal, this.currentEstimation, active);
  }
}
