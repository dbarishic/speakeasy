package com.dbarishic.speakeasy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO containing request object for API Gateway.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {
    private String language;
    private String message;

    // only needed to check if source is 'aws.events' and ignore the invocation
    private String source;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
