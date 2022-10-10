/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.repository;

import java.util.Optional;
import java.util.UUID;

import io.diveni.backend.model.Session;
import org.springframework.data.mongodb.repository.*;

public interface SessionRepository extends MongoRepository<Session, String> {

  Session findBySessionID(String sessionID);

  Session findByAdminCookie(UUID adminCookie);

  default Optional<Session> findByMemberID(String memberID) {
    return findAll().stream()
        .filter(s -> s.getMembers().stream().anyMatch(m -> m.getMemberID().equals(memberID)))
        .findFirst();
  }
}
