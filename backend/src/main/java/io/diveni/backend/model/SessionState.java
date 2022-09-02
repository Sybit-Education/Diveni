/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

public enum SessionState {
  WAITING_FOR_MEMBERS,
  START_VOTING,
  VOTING_FINISHED,
  SESSION_CLOSED,
}
