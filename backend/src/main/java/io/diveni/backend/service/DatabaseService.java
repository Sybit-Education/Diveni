/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.diveni.backend.model.Session;
import io.diveni.backend.model.Statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.diveni.backend.repository.SessionRepository;
import io.diveni.backend.repository.StatisticRepository;

@Service
public class DatabaseService {

  private final String stat_V1 = "STAT_V1";

  @Autowired SessionRepository sessionRepo;

  @Autowired StatisticRepository statisticRepository;


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
    statisticRepository.save(getOrCreateStatistic().incrementOverallSessions());
    sessionRepo.delete(session);
  }

  public void addRemovedMember() {
    statisticRepository.save(getOrCreateStatistic().incrementOverallAttendees());
  }

  public Integer getRemovedMember() {
    return statisticRepository.findById(stat_V1)
      .map(Statistic::getOverallAttendees)
      .orElse(0);
  }

  public Integer getDeletedSessions() {
    return statisticRepository.findById(stat_V1)
      .map(Statistic::getOverallSessions)
      .orElse(0);
  }

  private Statistic getOrCreateStatistic() {
    return statisticRepository.findById(stat_V1)
      .orElseGet(() -> {
        Statistic statistic = new Statistic();
        return statisticRepository.save(statistic);
      });
  }
}
