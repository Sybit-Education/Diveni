package io.diveni.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@Document("data")
public class DiveniData {

  private final int amountOfAttendees;

  private final int amountOfSessions;
}
