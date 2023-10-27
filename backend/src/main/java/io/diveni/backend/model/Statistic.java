package io.diveni.backend.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@EqualsAndHashCode
@Document("statistics")
public class Statistic {


  @Id
  private String id = "STAT_V1";

  private Integer overallSessions = 0;

  private Integer overallAttendees = 0;


  public Statistic incrementOverallSessions() {
    overallSessions++;
    return this;
  }

  public Statistic incrementOverallAttendees() {
    overallAttendees++;
    return this;
  }

}
