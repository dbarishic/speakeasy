package com.dbarishic.speakeasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class SynthesizeSpeechFunction implements RequestHandler<Request, Response> {

    public Response handleRequest(final Request request, final Context context) {
        try {
            return new Response(request.getMessage(), 200);
        } catch (Exception e) {
            return new Response("{}", 500);
        }
    }
}
