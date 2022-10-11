/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.val;

public class AvatarAnimalTest {

  @Test
  public void avatarAnimal_ToStringWorks() throws Exception {
    val animal = AvatarAnimal.WOLF;
    assertEquals(animal.toString(), "WOLF");
  }
}
