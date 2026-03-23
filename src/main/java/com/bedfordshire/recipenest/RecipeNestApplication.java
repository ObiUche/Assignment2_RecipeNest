package com.bedfordshire.recipenest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootApplication
@EnableJpaAuditing
public class RecipeNestApplication {

    private final S3Client s3Client;

    public RecipeNestApplication(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public static void main(String[] args) {
        SpringApplication.run(RecipeNestApplication.class, args);
    }

    @Component
    public class CredentialsTest implements CommandLineRunner{

        @Override
        public void run(String... args) throws Exception {
            try{
                s3Client.listBuckets();
                System.out.println("Credentials Working");

            } catch (Exception e){
                System.out.println("Not working : "+ e.getMessage());
            }
        }
    }
}
