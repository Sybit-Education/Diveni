package io.diveni.backend.controller;

import io.diveni.backend.model.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.*;

import io.diveni.backend.Utils;
import io.diveni.backend.repository.SessionRepository;
import io.diveni.backend.service.DatabaseService;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AnalyticsControllerTest {

  @Autowired SessionRepository sessionRepo;

  @Autowired private MockMvc mockMvc;

  @Autowired DatabaseService databaseService;

  @Test
  public void getDiveniData_oneSessionWith0Attendees() throws Exception {

    sessionRepo.deleteAll();
    sessionRepo.save(
        new Session(
            new ObjectId(),
            Utils.generateRandomID(),
            Utils.generateRandomID(),
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            LocalDate.of(2000, 12, 12),
            false,
            null));
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessions").value(1))
        .andExpect(jsonPath("$.amountOfAttendees").value(0));
  }

  @Test
  public void getDiveniData_oneSessionWith0AttendeesAndOneDeletedSessionWith2Attendees()
      throws Exception {
    sessionRepo.deleteAll();
    sessionRepo.save(
        new Session(
            new ObjectId(),
            Utils.generateRandomID(),
            Utils.generateRandomID(),
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            LocalDate.of(2000, 12, 12),
            false,
            null));
    Session willBeDelted =
        new Session(
            new ObjectId(),
            Utils.generateRandomID(),
            Utils.generateRandomID(),
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(new Member(), new Member()),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            LocalDate.of(2000, 12, 12),
            false,
            null);
    databaseService.saveSession(willBeDelted);
    databaseService.deleteSession(willBeDelted);
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessionsCurrently").value(1))
        .andExpect(jsonPath("$.amountOfSessions").value(2))
        .andExpect(jsonPath("$.amountOfAttendees").value(2));
  }

  @Test
  public void getCurrentDiveniData_oneSessionWith3Attendees() throws Exception {
    sessionRepo.deleteAll();
    sessionRepo.save(
        new Session(
            new ObjectId(),
            Utils.generateRandomID(),
            Utils.generateRandomID(),
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(new Member(), new Member(), new Member()),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            LocalDate.of(2000, 12, 12),
            false,
            null));
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessionsCurrently").value(1))
        .andExpect(jsonPath("$.amountOfAttendeesCurrently").value(3));
  }

  @Test
  public void
      getCurrentDiveniData_oneSessionWith2AttendeesAndOneDeletedSessionGetOnlyTheStillRunningSession()
          throws Exception {
    sessionRepo.deleteAll();
    sessionRepo.save(
        new Session(
            new ObjectId(),
            Utils.generateRandomID(),
            Utils.generateRandomID(),
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(new Member(), new Member()),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            LocalDate.of(2000, 12, 12),
            false,
            null));

    Session willBeDelted =
        new Session(
            new ObjectId(),
            Utils.generateRandomID(),
            Utils.generateRandomID(),
            new SessionConfig(new ArrayList<>(), List.of(), 10, "US_MANUALLY", null),
            null,
            List.of(new Member()),
            new HashMap<>(),
            new ArrayList<>(),
            SessionState.WAITING_FOR_MEMBERS,
            null,
            null,
            null,
            LocalDate.of(2000, 12, 12),
            false,
            null);
    databaseService.saveSession(willBeDelted);
    databaseService.deleteSession(willBeDelted);
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessionsCurrently").value(1))
        .andExpect(jsonPath("$.amountOfAttendeesCurrently").value(2));
  }

}
