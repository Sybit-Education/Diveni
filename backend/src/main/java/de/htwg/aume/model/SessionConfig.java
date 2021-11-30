package de.htwg.aume.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SessionConfig {

    private final List<String> set;

    private final List<UserStory> userStories;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String password;
}
