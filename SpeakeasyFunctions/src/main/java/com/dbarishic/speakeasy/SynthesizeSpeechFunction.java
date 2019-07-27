package com.dbarishic.speakeasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.VoiceId;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Handler for requests to Lambda function.
 */
public class SynthesizeSpeechFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static Logger log = LoggerFactory.getLogger(SynthesizeSpeechFunction.class);

    private final PollyClient myPollyClient = PollyClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .httpClient(ApacheHttpClient.builder().build())
            .build();

    // Synthesize speech and return it as a base64 encoded string
    @Override
    public APIGatewayProxyResponseEvent handleRequest (APIGatewayProxyRequestEvent requestEvent, Context context) {
        final ObjectMapper mapper = new ObjectMapper();

        // deconstruct request
        Request request = new Request();

        try {
            request = mapper.readValue(requestEvent.getBody(), Request.class);
        } catch (IOException e) {
            log.debug("context", e);
        }

        final SynthesizeSpeechRequest synthesizeSpeechRequest = SynthesizeSpeechRequest.builder()
                .outputFormat(OutputFormat.MP3)
                .voiceId(VoiceId.JOANNA)
                .text(request.getMessage())
                .build();

        final String fileName = "/tmp/" + LocalDateTime.now().toString() + ".mp3";
        final File synthesizedSpeechMP3 = new File(fileName);

        myPollyClient.synthesizeSpeech(synthesizeSpeechRequest, synthesizedSpeechMP3.toPath());

        // construct response
        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        final String synthesizedSpeechBase64String = fileToBase64(synthesizedSpeechMP3);
        response.setIsBase64Encoded(true);
        response.setBody(synthesizedSpeechBase64String);
        response.setStatusCode(200);

        return response;
    }

    private String fileToBase64(File file) {
        byte[] synthesizedSpeechMP3AsByteArray = new byte[0];
        try {
            synthesizedSpeechMP3AsByteArray = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.debug("context", e);
        }

        return Base64.getEncoder().encodeToString(synthesizedSpeechMP3AsByteArray);
    }
}