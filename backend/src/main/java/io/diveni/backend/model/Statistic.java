package io.diveni.backend.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Document("statistics")
public class Statistic {

  @Id private String id;

  private Integer overallSessions;

  private Integer overallAttendees;

  public Statistic incrementOverallSessions() {
    overallSessions++;
    return this;
  }

  public Statistic incrementOverallAttendees() {
    overallAttendees++;
    return this;
  }

  public Statistic addOverallAttendees(int overallAttendees) {
    this.overallAttendees += overallAttendees;
    return this;
  }
}
