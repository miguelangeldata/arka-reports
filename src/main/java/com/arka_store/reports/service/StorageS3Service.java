package com.arka_store.reports.service;

import com.arka_store.reports.cases.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;


@Service
@RequiredArgsConstructor
@Slf4j
public class StorageS3Service implements StorageService {

    private final S3Client s3Client;

    @Value("${s3.endpoint}")
    private String localstackEndpoint;

    @Value("${s3.bucket-name}")
    private String defaultBucketName;

    @Override
    public String uploadFile(File file, String targetBucketName, String key) {
        createBucketIfNotExist(targetBucketName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(targetBucketName)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

        String fileUrl = String.format("%s/%s/%s", localstackEndpoint, targetBucketName, key);
        log.info("File uploaded to LocalStack: {}", fileUrl);
        return fileUrl;
    }

    private void createBucketIfNotExist(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                log.warn("Bucket '{}' not found. Creating it...", bucketName);
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created successfully.", bucketName);
            } else {
                throw e;
            }
        }
    }
}
