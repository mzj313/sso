package com.chinasofti.oauth2.common.message.types;

/**
 * Created by yangkai on 15/5/20.
 */
public enum ParameterStyle {
    BODY("body"),
    QUERY("query"),
    HEADER("header");

    private String parameterStyle;

    private ParameterStyle(String parameterStyle) {
        this.parameterStyle = parameterStyle;
    }

    public String toString() {
        return this.parameterStyle;
    }
}
