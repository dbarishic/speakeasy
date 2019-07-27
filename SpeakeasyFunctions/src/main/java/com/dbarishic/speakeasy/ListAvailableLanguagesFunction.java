package com.dbarishic.speakeasy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.Voice;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class ListAvailableLanguagesFunction {

    Logger log = LoggerFactory.getLogger(ListAvailableLanguagesFunction.class);

    private PollyClient client = PollyClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .httpClient(ApacheHttpClient.builder().build())
            .build();

    public Response handleRequest(final Request request) {
        Set<String> voices = getlanguageNames();
        return new Response(voices.toString(), 200);
    }

    private Set<String> getlanguageNames() {
        DescribeVoicesRequest allVoicesRequest = DescribeVoicesRequest.builder().build();

        Set<Voice> voices = new HashSet<>();
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
