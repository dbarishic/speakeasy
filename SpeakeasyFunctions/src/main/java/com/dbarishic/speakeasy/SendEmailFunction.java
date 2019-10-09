package com.dbarishic.speakeasy;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import io.netty.handler.ssl.SslProvider;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Triggered on a s3:createObject event with a mp3 file of synthesized speech
 * send an email with the mp3 attached to the person that initiated the speech synthesis
 */
public class SendEmailFunction implements RequestHandler<S3Event, Void> {
    // TODO: Implement
    private static final SesClient sesClient = SesClient.builder()
            .httpClient(ApacheHttpClient.builder().build())
            .region(Region.EU_WEST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

    private static final S3Client myS3Client = S3Client.builder()
            .httpClient(UrlConnectionHttpClient.builder().build())
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .build();

    private static final DynamoDbClient myDynamoDbClient = DynamoDbClient.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build();

    private static Logger log = LoggerFactory.getLogger(SynthesizeSpeechFunction.class);


    @Override
    public Void handleRequest(S3Event s3Event, Context context) {

        S3EventNotificationRecord record = s3Event.getRecords().get(0);

        String objKey = record.getS3().getObject().getKey();
        String objBucket = record.getS3().getBucket().getName();

        // we get the object key url-encoded, so we decode it
        try {
            objKey = URLDecoder.decode(objKey, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.debug("context", e);
        }

        String mp3FileUrl = getFileUrlFromS3(objBucket, objKey);

        log.info("FILE NAME TO SEND = " + objKey);
        // file name is fileHash.S3ObjKey
        String[] parts = objKey.split("\\.");
        String fileHash = parts[0];

        log.info("FILE HASH FOR DYNAMO = " + fileHash);

        String destinationEmail = getEmailFromDynamo(fileHash);
        String sourceEmail = "no-reply@speakizi.me";
        String subject = "Your synthesized speech mp3 file.";
        String emailBody = "Here you can get your speech from using speakizi.me to synthesize a mp3 file to audio\n"
                + " " + mp3FileUrl;

        sendEmail(destinationEmail, sourceEmail, subject, emailBody);

        return null;
    }

    private void sendEmail(String destinationEmail, String sourceEmail, String subject, String emailText) {
        Destination destination = Destination.builder()
                .toAddresses(destinationEmail)
                .build();

        Content bodyContent = Content.builder()
                .data(emailText)
                .charset(StandardCharsets.UTF_8.name())
                .build();

        Content subjectContent = Content.builder()
                .data(subject)
                .charset(StandardCharsets.UTF_8.name())
                .build();

        Body emailBody = Body.builder()
                .text(bodyContent)
                .build();

        Message message = Message.builder()
                .body(emailBody)
                .subject(subjectContent)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .destination(destination)
                .source(sourceEmail)
                .message(message)
                .build();

        sesClient.sendEmail(request);
    }

    private String getEmailFromDynamo(String tableKey) {
        HashMap<String, AttributeValue> dynamoKey =
                new HashMap<>();

        dynamoKey.put("fileHash", AttributeValue.builder()
                .s(tableKey).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .key(dynamoKey)
                .tableName(System.getenv("DYNAMODB_TABLE_NAME"))
                .build();

        Map<String, AttributeValue> dynamoDbResponse = myDynamoDbClient.getItem(getItemRequest).item();

        return dynamoDbResponse.get("email").s();
    }

    private String getFileUrlFromS3(String objBucket, String objKey) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(objBucket)
                .key(objKey)
                .build();

        return myS3Client.utilities().getUrl(getUrlRequest).toExternalForm();
    }
}
