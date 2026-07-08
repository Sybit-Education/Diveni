/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.val;

@SpringBootTest
public class PasswordEncoderConfigTest {

  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  public void passwordEncoderBean_isConfigured() {
    assertNotNull(passwordEncoder);
  }

  @Test
  public void bcryptEncoder_encodesAndMatches() {
    val rawPassword = "testPassword123!";
    val encoded = passwordEncoder.encode(rawPassword);

    assertNotNull(encoded);
    assertTrue(passwordEncoder.matches(rawPassword, encoded));
    assertTrue(
        encoded.startsWith("$2a$") || encoded.startsWith("$2b$"),
        "Should be a BCrypt hash starting with $2a$ or $2b$");
  }

  @Test
  public void bcryptEncoder_rejectsWrongPassword() {
    val encoded = passwordEncoder.encode("correctPassword");
    assertTrue(passwordEncoder.matches("correctPassword", encoded));
    assertFalse(passwordEncoder.matches("wrongPassword", encoded));
  }
}
