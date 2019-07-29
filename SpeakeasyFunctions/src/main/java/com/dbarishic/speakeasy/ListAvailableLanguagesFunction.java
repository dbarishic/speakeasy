package com.dbarishic.speakeasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.Voice;

import java.util.*;
import java.util.stream.Collectors;


public class ListAvailableLanguagesFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static Logger log = LoggerFactory.getLogger(ListAvailableLanguagesFunction.class);

    private final PollyClient client = PollyClient.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .httpClient(ApacheHttpClient.builder().build())
            .build();

    @Override
    public APIGatewayProxyResponseEvent handleRequest (APIGatewayProxyRequestEvent requestEvent, Context context) {
        final ObjectMapper mapper = new ObjectMapper();

        final Map<String, String> languages = getLanguages();
        final Map<String, Map<String, String>> data = new HashMap<>();
        data.put("languages", languages);

        String dataJson = null;
        try {
            dataJson = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.debug("context", e);
        }

        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setBody(dataJson);
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);

        return response;
    }

    private Map<String, String> getLanguages() {
        final DescribeVoicesRequest allVoicesRequest = DescribeVoicesRequest.builder().build();

        final List<Voice> voices = new ArrayList<>();
        Map<String, String> languages = new HashMap<>();
        try {
            String nextToken;
            do {
                DescribeVoicesResponse allVoicesResult = client.describeVoices(allVoicesRequest);
                nextToken = allVoicesResult.nextToken();
                allVoicesResult.toBuilder().nextToken(nextToken);
                voices.addAll(allVoicesResult.voices());
                List<Voice> voicesFiltered = io.vavr.collection.List.ofAll(voices).distinctBy(Voice::languageCode).toJavaList();
                languages = voicesFiltered.stream().collect(Collectors.toMap(Voice::languageName, Voice::languageCodeAsString));
            } while (nextToken != null);
        } catch (Exception e) {
            log.debug("context", e);
        }

        return languages;
    }
}
