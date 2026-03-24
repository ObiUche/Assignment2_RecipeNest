package com.bedfordshire.recipenest.configTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AwsConfigTest {

    @Autowired
    private S3Client S3client;

    @Test
    void testS3ClientCanConnect(){

        ListBucketsResponse response = S3client.listBuckets();
        assertThat(response.buckets()).isNotNull();
        System.out.println("Successfully connected to AWS found " + response.buckets().size() + " buckets");
    }
}
