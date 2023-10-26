/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@Document("sessions")
public class Session {

  @Id private final ObjectId databaseID;

  private final String sessionID;

  private final String adminID;

  private final SessionConfig sessionConfig;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private final UUID adminCookie;

  private final List<Member> members;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private final Map<String, Integer> memberVoted;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private final List<String> currentHighlights;

  private final SessionState sessionState;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private final Date lastModified;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private final String accessToken;

  private final String timerTimestamp;

  private final LocalDate creationTime;

  static Comparator<String> estimationByIndex(List<String> set) {
    return Comparator.comparingInt((str) -> set.indexOf(str));
  }

  static Stream<String> getFilteredEstimationStream(List<Member> members) {
    return members.stream()
        .map(Member::getCurrentEstimation)
        .filter((estimation) -> estimation != null && !estimation.equals("?"));
  }

  public Session updateEstimation(String memberID, String vote) {
    val updatedMembers =
        members.stream()
            .map(m -> m.getMemberID().equals(memberID) ? m.updateEstimation(vote) : m)
            .collect(Collectors.toList());
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        updatedMembers,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session selectHighlightedMembers() {
    for (Member member : this.members) {
      memberVoted.putIfAbsent(member.getMemberID(), 0);
    }

    val filteredSet =
        sessionConfig.getSet().stream()
            .filter((string) -> !string.equals("?"))
            .collect(Collectors.toList());

    Optional<String> maxEstimation =
        getFilteredEstimationStream(this.members).max(estimationByIndex(filteredSet));
    Optional<String> minEstimation =
        getFilteredEstimationStream(this.members).min(estimationByIndex(filteredSet));

    if (maxEstimation.equals(minEstimation)) {
      return new Session(
          databaseID,
          sessionID,
          adminID,
          sessionConfig,
          adminCookie,
          members,
          memberVoted,
          new ArrayList<>(),
          sessionState,
          lastModified,
          accessToken,
          timerTimestamp,
          creationTime);
    }
    val maxEstimationMembers =
        this.members.stream()
            .filter(
                (member) ->
                    member.getCurrentEstimation() != null
                        && member.getCurrentEstimation().equals(maxEstimation.get()))
            .collect(Collectors.toList());
    val maxOptions =
        memberVoted.entrySet().stream()
            .filter(
                (entry) ->
                    maxEstimationMembers.stream()
                        .anyMatch((member) -> member.getMemberID().equals(entry.getKey())))
            .collect(Collectors.toList());
    val maxMemberID =
        Collections.max(maxOptions, Comparator.comparingInt(Map.Entry::getValue)).getKey();

    val minEstimationMembers =
        this.members.stream()
            .filter(
                (member) ->
                    member.getCurrentEstimation() != null
                        && member.getCurrentEstimation().equals(minEstimation.get()))
            .collect(Collectors.toList());
    val minOptions =
        memberVoted.entrySet().stream()
            .filter(
                (entry) ->
                    minEstimationMembers.stream()
                        .anyMatch((member) -> member.getMemberID().equals(entry.getKey())))
            .collect(Collectors.toList());
    val minMemberID =
        Collections.max(minOptions, Comparator.comparingInt(Map.Entry::getValue)).getKey();

    val newVoted =
        memberVoted.entrySet().stream()
            .map(
                (entry) -> {
                  if (entry.getKey().equals(maxMemberID) || entry.getKey().equals(minMemberID)) {
                    return Map.entry(entry.getKey(), 0);
                  } else {
                    return Map.entry(entry.getKey(), entry.getValue() + 1);
                  }
                })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    val newHighlighted = List.of(minMemberID, maxMemberID);

    val sortedMembers =
        members.stream()
            .sorted(
                (Member m1, Member m2) -> {
                  boolean b1 = newHighlighted.contains(m1.getMemberID());
                  boolean b2 = newHighlighted.contains(m2.getMemberID());
                  if (b1 == b2) {
                    return 0;
                  }
                  return b1 ? -1 : 1;
                })
            .collect(Collectors.toList());
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        sortedMembers,
        newVoted,
        newHighlighted,
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session resetCurrentHighlights() {
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        members,
        memberVoted,
        new ArrayList<>(),
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session updateUserStories(List<UserStory> userStories) {
    val updatedSessionConfig =
        new SessionConfig(
            sessionConfig.getSet(),
            userStories,
            sessionConfig.getTimerSeconds().orElse(null),
            sessionConfig.getUserStoryMode(),
            sessionConfig.getPassword());
    return new Session(
        databaseID,
        sessionID,
        adminID,
        updatedSessionConfig,
        adminCookie,
        members,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session resetEstimations() {
    val updatedMembers =
        members.stream().map(m -> m.resetEstimation()).collect(Collectors.toList());
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        updatedMembers,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session updateMembers(List<Member> updatedMembers) {
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        updatedMembers,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session updateSessionState(SessionState updatedSessionState) {
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        members,
        memberVoted,
        currentHighlights,
        updatedSessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session addMember(Member member) {
    var updatedMembers = new ArrayList<>(members);
    updatedMembers.add(member);
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        updatedMembers,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session removeMember(String memberID) {
    val updatedMembers =
        members.stream()
            .filter(m -> !m.getMemberID().equals(memberID))
            .collect(Collectors.toList());
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        updatedMembers,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session setTimerTimestamp(String timestamp) {
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        members,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        timestamp,
        creationTime);
  }

  public Session resetTimerTimestamp() {
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        members,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        null,
        creationTime);
  }

  public Session setLastModified(Date lastModified) {
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        members,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        accessToken,
        timerTimestamp,
        creationTime);
  }

  public Session setAccessToken(String token) {
    return new Session(
        databaseID,
        sessionID,
        adminID,
        sessionConfig,
        adminCookie,
        members,
        memberVoted,
        currentHighlights,
        sessionState,
        lastModified,
        token,
        timerTimestamp,
        creationTime);
  }
}
