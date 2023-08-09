package io.diveni.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class JiraConfig {

  @NotNull
  String jiraUrl;

  @NotNull
  String accessToken;

  @NotNull
  String consumerKey;

  @NotNull
  String privateKey;
}
