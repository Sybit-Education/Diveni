/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.diveni.backend.model.Session;
import io.diveni.backend.principals.MemberPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.diveni.backend.repository.SessionRepository;

@Service
public class DatabaseService {

  @Autowired SessionRepository sessionRepo;

  private List<Session> deletedSessions = new ArrayList<>();

  private List<MemberPrincipal> removedMembers = new ArrayList<>();

  public Optional<Session> getSessionByID(String sessionID) {
    return Optional.ofNullable(sessionRepo.findBySessionID(sessionID));
  }

  public Optional<Session> getSessionByAdminCookie(UUID adminCookie) {
    return Optional.ofNullable(sessionRepo.findByAdminCookie(adminCookie));
  }

  public Optional<Session> getSessionByMemberID(String memberID) {
    return sessionRepo.findByMemberID(memberID);
  }

  public List<Session> getSessions() {
    return sessionRepo.findAll();
  }

  public Session saveSession(Session session) {
    return sessionRepo.save(session.setLastModified(new Date()));
  }

  public void deleteSession(Session session) {
    deletedSessions.add(session);
    sessionRepo.delete(session);
  }

  public void addRemovedMember(MemberPrincipal removedMember) {
    this.removedMembers.add(removedMember);
  }

  public List<MemberPrincipal> getRemovedMember() {
    return this.removedMembers;
  }

  public List<Session> getDeletedSessions() {
    return this.deletedSessions;
  }
}
