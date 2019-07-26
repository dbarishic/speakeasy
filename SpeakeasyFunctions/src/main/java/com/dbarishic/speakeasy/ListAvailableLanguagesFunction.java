package com.dbarishic.speakeasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.Voice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class ListAvailableLanguagesFunction implements RequestHandler<Request, Response> {
    private AmazonPolly client = AmazonPollyClientBuilder.defaultClient();

    public Response handleRequest(final Request request, final Context context) {
        Set<String> voices = getLanguageCodes();
        return new Response(voices.toString(), 200);
    }

    private Set<String> getLanguageCodes() {
        DescribeVoicesRequest allVoicesRequest = new DescribeVoicesRequest();

        Set<Voice> voices = new HashSet<>();
        Set<String> languageCodes = new HashSet<>();
        try {
            String nextToken;
            do {
                DescribeVoicesResult allVoicesResult = client.describeVoices(allVoicesRequest);
                nextToken = allVoicesResult.getNextToken();
                allVoicesRequest.setNextToken(nextToken);
                voices.addAll(allVoicesResult.getVoices());
                languageCodes = voices.stream().map(Voice::getLanguageCode).collect(Collectors.toSet());
            } while (nextToken != null);
        } catch (Exception e) {
            System.err.println("Exception caught: " + e);
        }

        return languageCodes;
    }
}
