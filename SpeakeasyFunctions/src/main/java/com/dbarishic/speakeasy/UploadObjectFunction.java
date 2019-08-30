package com.dbarishic.speakeasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


/**
 * Generates a presigned S3 upload url with given email, fileName and languageCode
 * languageCode is needed later for voice synthesis of the uploaded file
 */
public class UploadObjectFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final DynamoDbClient myDynamoDbClient = DynamoDbClient.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build();

    private static final Logger log = LoggerFactory.getLogger(SynthesizeSpeechFunction.class);

    private static final Moshi moshi = new Moshi.Builder().build();
    private static final JsonAdapter<UploadFileRequest> requestJsonAdapter = moshi.adapter(UploadFileRequest.class);
    private static final JsonAdapter<Map> responseJsonAdapter = moshi.adapter(Map.class);


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        // deconstruct request
        UploadFileRequest request = null;
        try {
            request = requestJsonAdapter.fromJson(requestEvent.getBody());
        } catch (IOException e) {
            log.debug("context", e);
        }

        // generate fileHash
        String fileHashDelimiter = "$!$";
        String S3Key = request.getEmail() + fileHashDelimiter + request.getFileName() + fileHashDelimiter + request.getLanguage();
        String fileHash = DigestUtils.md5Hex(S3Key);

        // insert data into dynamodb
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("fileHash", AttributeValue.builder().s(fileHash).build());
        itemValues.put("email", AttributeValue.builder().s(request.getEmail()).build());
        itemValues.put("language", AttributeValue.builder().s(request.getLanguage()).build());
        itemValues.put("fileName", AttributeValue.builder().s(request.getFileName()).build());

        final String NOT_PROCESSED = "0";
        itemValues.put("processed", AttributeValue.builder().n(NOT_PROCESSED).build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(System.getenv("DYNAMODB_TABLE_NAME"))
                .item(itemValues)
                .build();

        try {
            myDynamoDbClient.putItem(putItemRequest);
        } catch (ResourceNotFoundException e) {
            log.error("Error: The table can't be found. TABLENAME: " + System.getenv("DYNAMODB_TABLE_NAME"));
            System.exit(1);
        } catch (DynamoDbException e) {
            log.debug("TABLENAME: " + System.getenv("DYNAMODB_TABLE_NAME"), e);
            System.exit(1);
        }

        // generate presigned upload url
        URI url = S3Utils.presign(S3Utils.PresignUrlRequest.builder()
                .bucket(System.getenv("BUCKET_NAME"))
                .key(S3Key)
                .region(Region.EU_WEST_1)
                .httpMethod(SdkHttpMethod.PUT)
                .signatureDuration(Duration.ofMinutes(15))
                .build());

        Map<String, String> body = new HashMap<>();
        body.put("url", url.toASCIIString());

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        final Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "*");
        headers.put("Access-Control-Allow-Methods", "OPTIONS, POST, PUT");

        response.setHeaders(headers);
        response.setBody(responseJsonAdapter.toJson(body));
        response.setStatusCode(200);

        return response;
    }
}
