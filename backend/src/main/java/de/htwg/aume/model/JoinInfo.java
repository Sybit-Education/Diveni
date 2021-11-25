package de.htwg.aume.model;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JoinInfo {
    private final Optional<String> password;

    private final Member member;
}
