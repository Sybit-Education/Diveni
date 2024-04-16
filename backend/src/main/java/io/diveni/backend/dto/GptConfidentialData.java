package io.diveni.backend.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.util.List;

@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class GptConfidentialData {
  final String id;

  final String title;

  final String description;

  final String estimation;

  final Boolean isActive;

  final JSONObject confidentialData;

  final String language;

  final List<String> voteSet;
}
