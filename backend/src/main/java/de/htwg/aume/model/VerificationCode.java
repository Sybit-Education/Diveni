package de.htwg.aume.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VerificationCode {
    private final String code;
    private final String token;

    public String getCode(){
        return code;
    }

    public String getToken(){
        return token;
    }
}
