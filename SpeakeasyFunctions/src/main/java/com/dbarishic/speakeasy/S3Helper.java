package com.dbarishic.speakeasy;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;

public class S3Helper {

    private S3Helper() {
        throw new IllegalStateException("Utility class, do not instantiate.");
    }

    public static String getUrl(String bucketName, String path) {
        GetObjectRequest.Builder getRequestBuilder = GetObjectRequest.builder();
        getRequestBuilder.bucket(bucketName);
        getRequestBuilder.key(path);

        return getUrl(getRequestBuilder.build()).toExternalForm();
    }
    public static URL getUrl(GetObjectRequest request) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https");
        urlBuilder.append("://");
        urlBuilder.append("s3-eu-west-1.amazonaws.com");
        urlBuilder.append("/");
        urlBuilder.append(request.bucket());
        urlBuilder.append("/");
        urlBuilder.append(request.key());
        URL url;
        try {
            url = new URL(urlBuilder.toString());
            return url;
        } catch (MalformedURLException e) {

        }
        return null;
    }
}
