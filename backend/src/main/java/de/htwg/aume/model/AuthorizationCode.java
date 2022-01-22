package de.htwg.aume.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthorizationCode {
    private final String code;

    public String getCode(){
        return code;
    }
}
