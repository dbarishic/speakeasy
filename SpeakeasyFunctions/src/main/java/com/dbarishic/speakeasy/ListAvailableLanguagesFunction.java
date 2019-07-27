package com.dbarishic.speakeasy;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class ListAvailableLanguagesFunction {

    private static Logger log = LoggerFactory.getLogger(ListAvailableLanguagesFunction.class);

    private final PollyClient client = PollyClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .httpClient(ApacheHttpClient.builder().build())
            .build();

    public APIGatewayProxyResponseEvent handleRequest() {
        final ObjectMapper mapper = new ObjectMapper();

        final Set<String> languages = getlanguageNames();
        final Map<String, Set<String>> data = new HashMap<>();
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

    private Set<String> getlanguageNames() {
        final DescribeVoicesRequest allVoicesRequest = DescribeVoicesRequest.builder().build();

        final Set<Voice> voices = new HashSet<>();
        Set<String> languageNames = new HashSet<>();
        try {
            String nextToken;
            do {
                DescribeVoicesResponse allVoicesResult = client.describeVoices(allVoicesRequest);
                nextToken = allVoicesResult.nextToken();
                allVoicesResult.toBuilder().nextToken(nextToken);
                voices.addAll(allVoicesResult.voices());
                languageNames = voices.stream().map(Voice::languageName).collect(Collectors.toSet());
            } while (nextToken != null);
        } catch (Exception e) {
            log.debug("context", e);
        }

        return languageNames;
    }
}
