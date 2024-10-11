/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SessionConfig {

  private final List<String> set;

  private final List<UserStory> userStories;

  private final Integer timerSeconds;

  private final String userStoryMode;

  private final Boolean memberControl;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private final String password;

  public Optional<Integer> getTimerSeconds() {
    return Optional.of(timerSeconds);
  }
}
