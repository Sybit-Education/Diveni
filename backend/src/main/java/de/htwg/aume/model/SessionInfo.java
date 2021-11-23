package de.htwg.aume.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SessionInfo {
    private final String password;

    private final Member member;
}
