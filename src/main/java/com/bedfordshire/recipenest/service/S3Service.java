package com.bedfordshire.recipenest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3Service {

    // AWS S3 client used to upload files into the configured bucket
    private final S3Client s3Client;

    // Target bucket name loaded from app props
    @Value("${aws.s3.bucket}")
    private String bucketName;

    // CloudFront domain used to return a public CDN-backed image URL
    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    public S3Service(S3Client s3Client){
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file, String folder){
        // Stop empty uploads early so invalid files don't reach S3
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("File must not be empty");
        }

        try {
            // Build a safe object key like:
            //recipes/<uuid>-my-image.jpg
            String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "image");
            String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9.\\-]","_");
            String key = folder + "/" + UUID.randomUUID() + "-" + safeFilename;

            // Build the S3 upload request with useful metadata
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // Upload the raw bytes to S3
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            // Return the CloudFront URL that the frontend can directly use

            return "https://" + cloudFrontDomain + "/" + key;

        } catch (IOException e) {
            // Wrap low-level file reading issues in a simpler runtime exception :<
            // so the service layer can fail clearly without leaking IO details
            throw new RuntimeException("Failed to upload file to S3",e);
        }
    }

    public void deleteFileByUrl(String fileUrl){

        // Base url for stripping
        String baseUrl = "https://" + cloudFrontDomain + "/";

        // IF URL is empty fail fast
        if(fileUrl == null  || fileUrl.isEmpty()){
            throw new IllegalArgumentException("Url must not be empty");
        }

        // Confirm that inputted url is valid
        if(!fileUrl.contains(cloudFrontDomain)){
            throw new IllegalArgumentException("File URL does not belong to the configured cloudFront");
        }

        // convert the public URL to s3 key
        // for example: https://cdn/.../recipes/abc.jpg -> recipes/abc.jpg
        String photoId = fileUrl.replace(baseUrl, "");

       DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
               .bucket(bucketName)
               .key(photoId)
               .build();

       // Delete from S3 bucket

        s3Client.deleteObject(deleteObjectRequest);


    }

}
