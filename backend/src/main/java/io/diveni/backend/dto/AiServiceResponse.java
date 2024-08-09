package io.diveni.backend.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class AiServiceResponse {

  private final Boolean apiKeyValid;

  private final Boolean serviceAvailable;

}


