package de.htwg.aume.model;

import java.util.Optional;

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

    // Override because MongoRepository has a problem with Optional<String> as
    // property
    public Optional<String> getEstimation() {
        return Optional.ofNullable(this.estimation);
    }
}
