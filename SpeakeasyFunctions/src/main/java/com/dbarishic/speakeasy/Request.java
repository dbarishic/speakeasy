package com.dbarishic.speakeasy;

/**
 * POJO containing request object for API Gateway.
 */
public class Request {
    private String language;
    private String message;

    public Request() {

    }

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
}
