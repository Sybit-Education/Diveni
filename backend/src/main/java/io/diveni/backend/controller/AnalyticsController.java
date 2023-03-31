package io.diveni.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.diveni.backend.model.AnalyticsData;
import io.diveni.backend.model.Session;
import io.diveni.backend.principals.MemberPrincipal;
import io.diveni.backend.service.DatabaseService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutesController.class);

  @Autowired
  DatabaseService databaseService;

  @GetMapping(value = "/analytics/All")
  public ResponseEntity<AnalyticsData> getAllData() {
    LOGGER.debug("--> getAllData()");
    int amountOfAllSessions = databaseService.getSessions().size() + databaseService.getDeletedSessions().size();
    int amountOfAllAttendees = databaseService.getSessions().stream()
        .collect(Collectors.summingInt(s -> s.getMembers().size())); // existing Sessions
    amountOfAllAttendees += databaseService.getDeletedSessions().stream()
        .collect(Collectors.summingInt(deletedSess -> deletedSess.getMembers().size()));

    for (MemberPrincipal member : databaseService.getRemovedMember()) {
      for (Session session : databaseService.getDeletedSessions()) {
        if (member.getSessionID().equals(session.getSessionID())) {
          amountOfAllAttendees += 1;
        }
      }
    }

    int lastMonth = LocalDate.now().getMonthValue();
    int counter = 0;
    if (lastMonth == 1) {
      counter = 11;
    } else {
      counter = -1;
    }
    int lastMonth2 = lastMonth + counter;
    int year = LocalDate.now().getYear();
    List<Session> sessionFromLastMonth = databaseService.getSessions().stream()
        .filter(
            s -> s.getCreationTime().getMonthValue() == lastMonth2
                && s.getCreationTime().getYear() == year)
        .collect(Collectors.toList());

    List<Session> deletedsessionFromLastMonth = databaseService.getDeletedSessions().stream()
        .filter(
            s -> s.getCreationTime().getMonthValue() == lastMonth2
                && s.getCreationTime().getYear() == year)
        .collect(Collectors.toList());

    int amountOfSessionsLastMonth = sessionFromLastMonth.size() + deletedsessionFromLastMonth.size();
    int amountOfAttendeesLastMonth = sessionFromLastMonth.stream()
        .collect(Collectors.summingInt(s -> s.getMembers().size()));

    amountOfAttendeesLastMonth += deletedsessionFromLastMonth.stream()
        .collect(Collectors.summingInt(s -> s.getMembers().size()));

    for (MemberPrincipal member : databaseService.getRemovedMember()) {
      for (Session session : deletedsessionFromLastMonth) {
        if (member.getSessionID() == session.getSessionID()) {
          amountOfAttendeesLastMonth += 1;
        }
      }
    }

    int currentAmountOfSessions = databaseService.getSessions().size();
    int currentAmountOfAttendees = databaseService.getSessions().stream()
        .collect(Collectors.summingInt(s -> s.getMembers().size())); // existing Sessions
    LOGGER.debug("<-- getAllData()");
    return new ResponseEntity<AnalyticsData>(
        new AnalyticsData(
            amountOfAllAttendees,
            amountOfAllSessions,
            amountOfAttendeesLastMonth,
            amountOfSessionsLastMonth,
            currentAmountOfAttendees,
            currentAmountOfSessions),
        HttpStatus.OK);
  }
}
