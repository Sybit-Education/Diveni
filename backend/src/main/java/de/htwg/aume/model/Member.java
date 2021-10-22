package de.htwg.aume.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
// @Document("member")
public class Member {

    private final UUID memberID;
    private final String name;
    private final String hexColor;
    private final AvatarAnimal avatarAnimal;
    private final Optional<Integer> currentEstimation;

}
