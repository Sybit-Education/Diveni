package io.diveni.backend.model.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PullRequest {

  private int number;

  @JsonProperty("html_url")
  private String htmlUrl;

  private String title;

  @JsonProperty("merged_at")
  private LocalDateTime mergedAt;

}
