package io.diveni.backend.controller;

import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.*;

import io.diveni.backend.Utils;
import io.diveni.backend.model.Member;
import io.diveni.backend.model.Session;
import io.diveni.backend.model.SessionConfig;
import io.diveni.backend.model.SessionState;
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
            LocalDate.of(2000, 12, 12)));
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
            LocalDate.of(2000, 12, 12)));
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
            LocalDate.of(2000, 12, 12));
    databaseService.saveSession(willBeDelted);
    databaseService.deleteSession(willBeDelted);
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
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
            LocalDate.of(2000, 12, 12)));
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
            LocalDate.of(2000, 12, 12)));

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
            LocalDate.of(2000, 12, 12));
    databaseService.saveSession(willBeDelted);
    databaseService.deleteSession(willBeDelted);
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessionsCurrently").value(1))
        .andExpect(jsonPath("$.amountOfAttendeesCurrently").value(2));
  }

  @Test
  public void getDiveniDataFromLastMonth_oneSessionWith3Attendees() throws Exception {
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
            LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, 12)));
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessions").value(1))
        .andExpect(jsonPath("$.amountOfAttendees").value(3))
        .andExpect(jsonPath("$.amountofAttendeesLastMonth").value(3))
        .andExpect(jsonPath("$.amountOfSessionsLastMonth").value(1));
  }

  @Test
  public void
      getDiveniDataFromLastMonth_oneSessionWith3AttendeesAndOneSessionWith2AttendesButNotLastMonth()
          throws Exception {
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
            LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, 12)));

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
            LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 12)));

    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessions").value(2))
        .andExpect(jsonPath("$.amountOfAttendees").value(5))
        .andExpect(jsonPath("$.amountofAttendeesLastMonth").value(3))
        .andExpect(jsonPath("$.amountOfSessionsLastMonth").value(1));
    ;
  }

  @Test
  public void
      getDiveniDataFromLastMonth_oneSessionWith3AttendeesAndOneSessionWith2AttendesAndDeletedAndFromLastMonth()
          throws Exception {
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
            LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, 12)));

    Session willbeDelted =
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
            LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, 12));

    databaseService.saveSession(willbeDelted);
    databaseService.deleteSession(willbeDelted);
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessionsLastMonth").value(2))
        .andExpect(jsonPath("$.amountofAttendeesLastMonth").value(5));
  }

  @Test
  public void
      getDiveniDataFromLastMonth_oneSessionWith3AttendeesAndOneSessionWith2AttendesAndDeletedAndNotFromLastMonth()
          throws Exception {
    int lastMonth = LocalDate.now().getMonthValue() - 1;
    int outDatedMonth;
    if (lastMonth == 1) {
      outDatedMonth = 12;
    } else {
      outDatedMonth = lastMonth - 1;
    }
    sessionRepo.deleteAll();
    databaseService.saveSession(
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
            LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, 12)));

    Session willbeDelted =
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
            LocalDate.of(LocalDate.now().getYear(), outDatedMonth, 12));

    databaseService.saveSession(willbeDelted);
    databaseService.deleteSession(willbeDelted);
    this.mockMvc
        .perform(get("/analytics/All"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amountOfSessionsLastMonth").value(1))
        .andExpect(jsonPath("$.amountofAttendeesLastMonth").value(3));
  }
}
