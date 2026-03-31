package com.bedfordshire.recipenest.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    // This class turns on Spring Data JPA auditing
    // It allows fields like @CreatedData to be filled automatically
    // when an entity is first saved.

}
