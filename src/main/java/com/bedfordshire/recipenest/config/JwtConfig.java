package com.bedfordshire.recipenest.config;


import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    // Reads the JWT secret from application props
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey jwtSecretKey(){
        // HMAC SHA-256 needs a long secret
        // This check helps fail fast during startup.
        if(jwtSecret == null || jwtSecret.length() < 32){
            throw new IllegalStateException("JWT secret must be at least 32 chars long");
        }

        // Converts the raw secret string into a SecretKey Object
        // that Spring Security can use for signing and verifying JWTs.
        return new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey){
        // Used By Spring Security to validate and decode incoming Bearer token
        // This is what supports oauth2ResoucreServer().jwt() in SecurityConfig

        return NimbusJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey){
        // Used by Jwtservice
        // generate signed JWT access tokens during login
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

}
