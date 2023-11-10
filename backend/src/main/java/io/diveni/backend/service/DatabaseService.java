/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.diveni.backend.model.Session;
import io.diveni.backend.model.Statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.diveni.backend.repository.SessionRepository;
import io.diveni.backend.repository.StatisticRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

  //Sets the database entry identifier in our 'statistics' table.
  //Only modify if you want to create a new database entry!!!
  //Variable can be further extracted
  private final String statEntryIdentifier = "STAT_V1";

  @Autowired SessionRepository sessionRepo;

  @Autowired StatisticRepository statisticRepo;


  public Optional<Session> getSessionByID(String sessionID) {
    LOGGER.debug("getSessionByID()");
    return Optional.ofNullable(sessionRepo.findBySessionID(sessionID));
  }

  public Optional<Session> getSessionByAdminCookie(UUID adminCookie) {
    LOGGER.debug("getSessionByAdminCookie()");
    return Optional.ofNullable(sessionRepo.findByAdminCookie(adminCookie));
  }

  public Optional<Session> getSessionByMemberID(String memberID) {
    LOGGER.debug("getSessionByMemberID()");
    return sessionRepo.findByMemberID(memberID);
  }

  public List<Session> getSessions() {
    LOGGER.debug("getSessions()");
    return sessionRepo.findAll();
  }

  public Session saveSession(Session session) {
    LOGGER.debug("saveSession()");
    return sessionRepo.save(session.setLastModified(new Date()));
  }

  @Transactional
  public void deleteSession(Session session) {
    LOGGER.debug("--> deleteSession()");
    Statistic statistic = getOrCreateStatistic();
    statistic.incrementOverallSessions().addOverallAttendees(session.getMembers().size());
    statisticRepo.save(statistic);
    sessionRepo.delete(session);
    LOGGER.debug("<-- deleteSession()");
  }

  public void addRemovedMember()  {
    LOGGER.debug("--> addRemovedMember()");
    Statistic statistic = getOrCreateStatistic();
    statistic.incrementOverallAttendees();
    statisticRepo.save(statistic);
    LOGGER.debug("<-- addRemovedMember()");
  }

  public Integer getRemovedMember() {
    LOGGER.debug("getRemovedMember()");
    return statisticRepo.findById(statEntryIdentifier)
      .map(Statistic::getOverallAttendees)
      .orElse(0);
  }

  public Integer getDeletedSessions() {
    LOGGER.debug("getDeletedSessions()");
    return statisticRepo.findById(statEntryIdentifier)
      .map(Statistic::getOverallSessions)
      .orElse(0);
  }

  private Statistic getOrCreateStatistic() {
    LOGGER.debug("getOrCreateStatistic()");
    return statisticRepo.findById(statEntryIdentifier)
      .orElseGet(() -> statisticRepo.save(new Statistic(statEntryIdentifier, 0, 0, LocalDate.now())));
  }
}
