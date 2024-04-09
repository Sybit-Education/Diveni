package io.diveni.backend.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class GptConfidentalData {
  final String id;

  final String title;

  final String description;

  final String estimation;

  final Boolean isActive;

  final List<String> confidentalData;
}
