package com.chinasofti.oauth2.common.message.types;

/**
 * Created by yangkai on 15/5/20.
 */
public enum TokenType {
    BEARER("Bearer"),
    MAC("MAC");

    private String tokenType;

    private TokenType(String grantType) {
        this.tokenType = grantType;
    }

    public String toString() {
        return this.tokenType;
    }
}
