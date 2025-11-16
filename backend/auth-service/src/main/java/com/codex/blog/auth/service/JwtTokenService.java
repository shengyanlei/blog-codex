package com.codex.blog.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenService {

    private final JwtProperties properties;
    private Key key;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    public String generateToken(String userId, String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(userId)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(properties.getAccessTokenTtlSeconds())))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long accessTokenTtlSeconds() {
        return properties.getAccessTokenTtlSeconds();
    }
}
