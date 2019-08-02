package com.dbarishic.speakeasy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO containing request object for API Gateway.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {
    private String language;
    private String text;
    private String origin;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
