package io.diveni.backend.model.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;

  @JsonProperty("user_type")
  private String userType;

  @JsonProperty("user")
  private void unpackNestedProperty(Map<String, String> map) {
    userType = map.get("type");
  }

}
