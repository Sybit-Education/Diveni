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
                LOGGER.debug("--> getDiveniData()");
                int amountOfSessions1 = databaseService.getSessions().size()
                                + databaseService.getDeletedSessions().size();
                int amountOfAttendees1 = databaseService.getSessions().stream()
                                .collect(Collectors.summingInt(s -> s.getMembers().size())); // existing Sessions
                amountOfAttendees1 += databaseService.getDeletedSessions().stream()
                                .collect(Collectors.summingInt(deletedSess -> deletedSess.getMembers().size()));

                for (MemberPrincipal member : databaseService.getRemovedMember()) {
                        for (Session session : databaseService.getDeletedSessions()) {
                                if (member.getSessionID().equals(session.getSessionID())) {
                                        amountOfAttendees1 += 1;
                                }
                        }
                }
                LOGGER.debug("<-- getDiveniData()");

                LOGGER.debug("--> getDiveniDataFromLastMonth()");
                int lastMonth = LocalDate.now().getMonthValue();
                int counter = 0;
                if (lastMonth == 1) {
                        counter = 11;
                } else {
                        counter = -1;
                }
                int lastMonth2 = lastMonth + counter;
                int year = LocalDate.now().getYear();
                LOGGER.debug("letzter Monat: " + lastMonth2 + " Jahr: " + year);
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

                int amountOfSessions2 = sessionFromLastMonth.size() + deletedsessionFromLastMonth.size();
                int amountOfAttendees2 = sessionFromLastMonth.stream()
                                .collect(Collectors.summingInt(s -> s.getMembers().size()));

                amountOfAttendees2 += deletedsessionFromLastMonth.stream()
                                .collect(Collectors.summingInt(s -> s.getMembers().size()));

                for (MemberPrincipal member : databaseService.getRemovedMember()) {
                        for (Session session : deletedsessionFromLastMonth) {
                                if (member.getSessionID() == session.getSessionID()) {
                                        amountOfAttendees2 += 1;
                                }
                        }
                }
                LOGGER.debug("<-- getDiveniDataFromLastMont()");

                LOGGER.debug("--> getCurrentDiveniData()");
                int amountOfSessions3 = databaseService.getSessions().size();
                int amountOfAttendees3 = databaseService.getSessions().stream()
                                .collect(Collectors.summingInt(s -> s.getMembers().size())); // existing Sessions
                LOGGER.debug("<-- getCurrentDiveniData()");
                return new ResponseEntity<AnalyticsData>(
                                new AnalyticsData(amountOfAttendees1, amountOfSessions1, amountOfAttendees2,
                                                amountOfSessions2, amountOfAttendees3, amountOfSessions3),
                                HttpStatus.OK);
        }

}
