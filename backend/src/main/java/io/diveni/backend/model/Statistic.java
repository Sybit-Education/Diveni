/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Document("statistics")
public class Statistic {

  @Id private final String id;

  private final Integer overallSessions;

  private final Integer overallAttendees;

  private final LocalDate creationTime;


  public Statistic addOverallSessions(int addToOverallSessions) {
    return new Statistic(
      id,
      overallSessions + addToOverallSessions,
      overallAttendees,
      LocalDate.from(creationTime)
    );
  }

  public Statistic addOverallAttendees(int addToOverallAttendees) {
    return new Statistic(
      id,
      overallSessions,
      overallAttendees + addToOverallAttendees,
      LocalDate.from(creationTime)
    );
  }
}
