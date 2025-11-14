package io.diveni.backend.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CardSetType {

  FIBONACCI("fibonacci"),
  T_SHIRTS("t-shirts"),
  HOURS("hours"),
  NUMBERS("numbers"),
  CUSTOM("custom");

  private final String value;

  CardSetType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static CardSetType fromValue(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("CardSetType cannot be null or empty");
    }

    String normalized = value.trim();

    for (CardSetType type : values()) {
      if (type.value.equalsIgnoreCase(normalized)) {
        return type;
      }
    }

    throw new IllegalArgumentException("Unknown CardSetType: " + value);
  }
}
