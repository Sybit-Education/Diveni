package io.diveni.backend.controller;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.diveni.backend.model.AnalyticsData;
import io.diveni.backend.service.DatabaseService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsController.class);

  @Autowired DatabaseService databaseService;

  @GetMapping(value = "/analytics/All")
  public ResponseEntity<AnalyticsData> getAllData() {
    LOGGER.debug("--> getAllData()");

    int amountOfAllSessions =
        databaseService.getSessions().size() + databaseService.getDeletedSessions();
    int amountOfAllAttendees =
        databaseService.getSessions().stream()
            .collect(Collectors.summingInt(s -> s.getMembers().size()));
    amountOfAllAttendees += databaseService.getRemovedMember();

    int currentAmountOfSessions = databaseService.getSessions().size();
    int currentAmountOfAttendees =
        databaseService.getSessions().stream()
            .collect(Collectors.summingInt(s -> s.getMembers().size())); // existing Sessions
    LOGGER.debug("<-- getAllData()");
    return new ResponseEntity<>(
        new AnalyticsData(
            amountOfAllAttendees,
            amountOfAllSessions,
            currentAmountOfAttendees,
            currentAmountOfSessions),
        HttpStatus.OK);
  }
}
