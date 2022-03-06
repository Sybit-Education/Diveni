package io.diveni.backend.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SessionConfig {

    private final List<String> set;

    private final List<UserStory> userStories;

    private final Integer timerSeconds;

    public Optional<Integer> getTimerSeconds() {
        return Optional.of(timerSeconds);
    }

    private final String userStoryMode;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String password;
}
