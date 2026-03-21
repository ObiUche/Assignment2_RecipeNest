package com.bedfordshire.recipenest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client(){
        // Automaticaaly use creds from:
        // 1. Env
        // 2. ~/.aws/credentials file
        // 3. IAM role (if on EC2)

        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

}
