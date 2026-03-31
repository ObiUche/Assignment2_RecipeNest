package com.bedfordshire.recipenest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootApplication
public class RecipeNestApplication {




    public static void main(String[] args) {
        SpringApplication.run(RecipeNestApplication.class, args);
    }


}
