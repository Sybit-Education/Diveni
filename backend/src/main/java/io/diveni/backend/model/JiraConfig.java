package io.diveni.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JiraConfig {
  String jiraUrl;
  String accessToken;
  String consumerKey;
  String privateKey;
}
