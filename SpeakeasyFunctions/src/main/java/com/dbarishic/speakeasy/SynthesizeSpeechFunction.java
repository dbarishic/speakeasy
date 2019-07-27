package com.dbarishic.speakeasy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.VoiceId;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

/**
 * Handler for requests to Lambda function.
 */
public class SynthesizeSpeechFunction {

    Logger log = LoggerFactory.getLogger(SynthesizeSpeechFunction.class);

    private final PollyClient myPollyClient = PollyClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .httpClient(ApacheHttpClient.builder().build())
            .build();

    private final S3Client myS3Client = S3Client.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .httpClient(ApacheHttpClient.builder().build())
            .build();

    public Response handleRequest(final Request request) throws ExecutionException, InterruptedException {

        // Synthesize speech and save to a new mp3 file
        SynthesizeSpeechRequest synthesizeSpeechRequest = SynthesizeSpeechRequest.builder()
                .outputFormat(OutputFormat.MP3)
                .voiceId(VoiceId.JOANNA)
                .text(request.getMessage())
                .build();

        String fileName = "tmp/" + LocalDateTime.now().toString() + request.getLanguage() + ".mp3";
        File outputFile = new File(fileName);
        myPollyClient.synthesizeSpeech(synthesizeSpeechRequest, outputFile.toPath());

        // Upload generated mp3 to S3 bucket
        String myGeneratedMp3Url = null;
        try {
            String bucketName = System.getenv("BUCKET_NAME");

            myS3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(fileName).acl(ObjectCannedACL.PUBLIC_READ).build(),
                    RequestBody.fromFile(outputFile));

            myGeneratedMp3Url = S3Helper.getUrl(bucketName, fileName);
        } catch (SdkClientException e) {
            log.debug("context", e);
            return new Response("Error while processing request", 500);
        }
        return new Response(myGeneratedMp3Url, 200);
    }
}
