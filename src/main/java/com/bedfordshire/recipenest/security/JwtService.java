package com.bedfordshire.recipenest.security;


import com.bedfordshire.recipenest.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
public class JwtService {

    // Spring security component used to sign JWT tokens
    private final JwtEncoder jwtEncoder;

    // Value loaded from app.pros
    @Value("${security.jwt.issuer}")
    private String issuer;

    // Token validity period in seconds
    @Value("{jwt.expiration}")
    private long accessTokenExpirationSeconds;

    public JwtService(JwtEncoder jwtEncoder){
        this.jwtEncoder = jwtEncoder;
    }

    public String generateAccessToken(User user){
        // Current time used for issuedAt and expiresAt
        Instant now = Instant.now();

        // Extracts user authorizes from spring security
        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Build the JWT Payload (claims)
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer) // Who created the token
                .issuedAt(now) // token creation time
                .expiresAt(now.plusSeconds(accessTokenExpirationSeconds)) // expiry time
                .subject(user.getEmail()) // main identity of the token
                .claim("userId", user.getId()) // custom clam for app
                .claim("roles", roles) // custom claim used for RBAC
                .build();

        // Build JWT Header and choose signing algorithm
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        // Sign the token and return it as string
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();

    }

    public long getAccessTokenExpirationSeconds(){
        return accessTokenExpirationSeconds;
    }



}
