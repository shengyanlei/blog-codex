package com.codex.blog.auth.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenTtlSeconds;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }
}
