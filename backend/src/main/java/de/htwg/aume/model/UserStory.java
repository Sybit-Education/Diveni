package de.htwg.aume.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class UserStory {

    final String title;

    final String description;

    final String estimation;
    
    final Boolean isActive;

}
