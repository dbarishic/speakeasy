package com.dbarishic.speakeasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class UploadObjectFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static Logger log = LoggerFactory.getLogger(SynthesizeSpeechFunction.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        // deconstruct request
        UploadFileRequest request = new UploadFileRequest();

        try {
            request = mapper.readValue(requestEvent.getBody(), UploadFileRequest.class);
        } catch (IOException e) {
            log.debug("context", e);
        }

        URI url = S3Utils.presign(S3Utils.PresignUrlRequest.builder()
                .bucket("speakeasy-uploads")
                .key(request.getFileName())
                .region(Region.EU_WEST_1)
                .httpMethod(SdkHttpMethod.PUT)
                .signatureDuration(Duration.ofHours(1))
                .build());

        Map<String, URI> body = new HashMap<>();
        body.put("url", url);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(body.toString());

        return response;
    }
}
