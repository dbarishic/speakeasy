package com.dbarishic.speakeasy;

/**
 * POJO containing response object for API Gateway.
 */
public class Response {

    private final String body;
    private final int statusCode;

    public Response(final String body, final int statusCode) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
