package com.dbarishic.speakeasy;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.Instant;


/**
 * Utils to generate a presigned upload url for S3 because it is not yet supported by the
 * aws-sdk-java-v2, will probably be removed when the sdk starts supporting presigned urls
 */
public class S3Utils {

    public static URI presign(PresignUrlRequest request) {
        String encodedBucket, encodedKey;
        try {
            encodedBucket = URLEncoder.encode(request.bucket(), StandardCharsets.UTF_8.name());
            encodedKey = request.key();
            //encodedKey = URLEncoder.encode(request.key(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }

        SdkHttpFullRequest httpRequest =
                SdkHttpFullRequest.builder()
                        .method(request.httpMethod())
                        .protocol("https")
                        .host("s3-" + request.region().id() + ".amazonaws.com")
                        .encodedPath(encodedBucket + "/" + encodedKey)
                        .build();

        Instant expirationTime = request.signatureDuration() == null ? null : Instant.now().plus(request.signatureDuration());
        Aws4PresignerParams presignRequest =
                Aws4PresignerParams.builder()
                        .expirationTime(expirationTime)
                        .awsCredentials(request.credentialsProvider().resolveCredentials())
                        .signingName(S3Client.SERVICE_NAME)
                        .signingRegion(request.region())
                        .build();

        return AwsS3V4Signer.create().presign(httpRequest, presignRequest).getUri();
    }

    public static class PresignUrlRequest implements ToCopyableBuilder<PresignUrlRequest.Builder, PresignUrlRequest> {
        private final AwsCredentialsProvider credentialsProvider;
        private final SdkHttpMethod httpMethod;
        private final Region region;
        private final String bucket;
        private final String key;
        private final Duration signatureDuration;

        private PresignUrlRequest(Builder builder) {
            this.credentialsProvider = Validate.notNull(builder.credentialsProvider, "credentialsProvider");
            this.httpMethod = Validate.notNull(builder.httpMethod, "httpMethod");
            this.region = Validate.notNull(builder.region, "region");
            this.bucket = Validate.notNull(builder.bucket, "bucket");
            this.key = Validate.notNull(builder.key, "key");
            this.signatureDuration = builder.signatureDuration;
        }

        public static Builder builder() {
            return new Builder();
        }

        public AwsCredentialsProvider credentialsProvider() {
            return credentialsProvider;
        }

        public SdkHttpMethod httpMethod() {
            return httpMethod;
        }

        public Region region() {
            return region;
        }

        public String bucket() {
            return bucket;
        }

        public String key() {
            return key;
        }

        public Duration signatureDuration() {
            return signatureDuration;
        }

        @Override
        public Builder toBuilder() {
            return builder()
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .bucket(bucket)
                    .key(key)
                    .signatureDuration(signatureDuration);
        }

        public static class Builder implements CopyableBuilder<Builder, PresignUrlRequest> {
            private AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
            private SdkHttpMethod httpMethod = SdkHttpMethod.GET;
            private Region region;
            private String bucket;
            private String key;
            private Duration signatureDuration;

            public Builder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
                this.credentialsProvider = credentialsProvider;
                return this;
            }

            public Builder httpMethod(SdkHttpMethod httpMethod) {
                this.httpMethod = httpMethod;
                return this;
            }

            public Builder region(Region region) {
                this.region = region;
                return this;
            }

            public Builder bucket(String bucket) {
                this.bucket = bucket;
                return this;
            }

            public Builder key(String key) {
                this.key = key;
                return this;
            }

            public Builder signatureDuration(Duration signatureDuration) {
                this.signatureDuration = signatureDuration;
                return this;
            }

            @Override
            public PresignUrlRequest build() {
                return new PresignUrlRequest(this);
            }
        }
    }
}

