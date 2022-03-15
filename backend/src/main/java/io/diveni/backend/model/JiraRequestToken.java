package io.diveni.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JiraRequestToken {
    private String token;
    private String url;

    public void setToken(String token){
        this.token = token;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
