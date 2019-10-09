package com.dbarishic.speakeasy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

/**
 * Triggered on a s3:createObject event, get the uploaded file from s3, extracts text,
 * and sends an async request to polly to synthesize the extracted text to audio,
 * then uploads the audio file to another bucket
 */
public class SynthesizeDocumentFunction implements RequestHandler<S3Event, Optional> {

    private static Logger log = LoggerFactory.getLogger(SynthesizeSpeechFunction.class);

    private static final S3Client myS3Client = S3Client.builder()
            .httpClient(UrlConnectionHttpClient.builder().build())
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .build();

    private static final PollyClient myPollyClient = PollyClient.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClient(ApacheHttpClient.builder().build())
            .build();

    private static final DynamoDbClient myDynamoDbClient = DynamoDbClient.builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build();


    @Override
    public Optional handleRequest(S3Event s3Event, Context context) {

        S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);

        String objKey = record.getS3().getObject().getKey();

        try {
            objKey = URLDecoder.decode(objKey, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.debug("context", e);
        }

        String objBucket = record.getS3().getBucket().getName();

        File pdfFile = new File("/tmp/" + objKey);

        // get pdf file from S3
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(objBucket)
                .key(objKey)
                .build();

        myS3Client.getObject(getObjectRequest, ResponseTransformer.toFile(pdfFile));


        // extract text from pdf
        String parsedText = null;
        try {
            PDFParser parser = new PDFParser(new RandomAccessFile(pdfFile, "r"));
            parser.parse();
            COSDocument cosDocument = parser.getDocument();
            PDFTextStripper pdfStripper = new PDFTextStripper();
            PDDocument pdDocument = new PDDocument(cosDocument);
            parsedText = pdfStripper.getText(pdDocument);

            cosDocument.close();
            pdDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // fileHash used as dynamodb key and as a prefix for the output file in s3
        String fileHash = DigestUtils.md5Hex(objKey);

        String[] parts = objKey.split("\\$!\\$");

        String email = parts[0];
        String fileName = parts[1];
        String languageCode = parts[2];

        log.info("CALLING DESCRIBE VOICES WITH LANG_CODE: " + languageCode);
        // text to mp3 async
        DescribeVoicesRequest describeVoicesRequest = DescribeVoicesRequest.builder()
                .languageCode(languageCode)
                .build();

        VoiceId voiceId = myPollyClient.describeVoices(describeVoicesRequest).voices().get(0).id();

        StartSpeechSynthesisTaskRequest request = StartSpeechSynthesisTaskRequest.builder()
                .text(parsedText)
                .languageCode(languageCode)
                .outputFormat(OutputFormat.MP3)
                .outputS3BucketName(System.getenv("MP3_BUCKET_NAME"))
                .outputS3KeyPrefix(fileHash)
                .voiceId(voiceId)
                .build();

        StartSpeechSynthesisTaskResponse response = myPollyClient.startSpeechSynthesisTask(request);

        // update dynamodb record
        Optional<SynthesisTask> synthesisTask = Optional.ofNullable(response.synthesisTask());

        Optional<String> generatedMp3UriOpt = Optional.empty();
        if (synthesisTask.isPresent()) {
            generatedMp3UriOpt = Optional.ofNullable(response.synthesisTask().outputUri());
        }

        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();

        final String PROCESSED = "1";

        generatedMp3UriOpt.ifPresent(s -> updatedValues.put("outputURI", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(s).build())
                .action(AttributeAction.PUT)
                .build()));

        updatedValues.put("processed", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().n(PROCESSED).build())
                .action(AttributeAction.PUT)
                .build());

        HashMap<String, AttributeValue> itemKey = new HashMap<>();

        itemKey.put("fileHash", AttributeValue.builder().s(fileHash).build());
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(System.getenv("DYNAMODB_TABLE_NAME"))
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();

        try {
            myDynamoDbClient.updateItem(updateItemRequest);
        } catch (ResourceNotFoundException e) {
            log.error("Error: The table can't be found. TABLENAME: " + System.getenv("DYNAMODB_TABLE_NAME"));
            System.exit(1);
        } catch (DynamoDbException e) {
            log.debug("TABLENAME: " + System.getenv("DYNAMODB_TABLE_NAME"), e);
            System.exit(1);
        }

        return Optional.empty();
    }
}
