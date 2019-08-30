package com.dbarishic.speakeasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.Voice;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Lists all available languages provided by AWS Polly
 */
public class ListAvailableLanguagesFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    private static Logger log = LoggerFactory.getLogger(ListAvailableLanguagesFunction.class);

    private static final PollyClient client = PollyClient.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClient(ApacheHttpClient.builder().build())
            .build();


    private static final Moshi moshi = new Moshi.Builder().build();
    private static final JsonAdapter<Map> jsonAdapter = moshi.adapter(Map.class);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        final List<Language> languages = getLanguages();
        final Map<String, List<Language>> data = new HashMap<>();
        data.put("languages", languages);

        String dataJson = jsonAdapter.toJson(data);

        final Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");

        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(headers);
        response.setBody(dataJson);
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);

        return response;
    }

    private List<Language> getLanguages() {
        final DescribeVoicesRequest allVoicesRequest = DescribeVoicesRequest.builder().build();

        final List<Voice> voices = new ArrayList<>();
        Map<String, String> languagesMap = new HashMap<>();
        try {
            String nextToken;
            do {
                DescribeVoicesResponse allVoicesResult = client.describeVoices(allVoicesRequest);
                nextToken = allVoicesResult.nextToken();
                allVoicesResult.toBuilder().nextToken(nextToken);
                voices.addAll(allVoicesResult.voices());
                List<Voice> voicesFiltered = io.vavr.collection.List.ofAll(voices).distinctBy(Voice::languageCode).toJavaList();
                languagesMap = voicesFiltered.stream().collect(Collectors.toMap(Voice::languageName, Voice::languageCodeAsString));
            } while (nextToken != null);
        } catch (Exception e) {
            log.debug("context", e);
        }

        List<Language> languages = new ArrayList<>();
        languagesMap.forEach((key, value) -> {
            // key = langName, value = langCode
            languages.add(new Language(key, value));
        });
        return languages;
    }
}
