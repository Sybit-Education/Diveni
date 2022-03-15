package io.diveni.backend.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class UserStory {

    final String jiraId;

    final String title;

    final String description;

    final String estimation;

    final Boolean isActive;

}
