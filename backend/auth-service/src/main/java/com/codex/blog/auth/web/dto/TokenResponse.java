package com.codex.blog.auth.web.dto;

public class TokenResponse {

    private final String accessToken;
    private final long expiresIn;
    private final String refreshToken;
    private final boolean mfaRequired;

    public TokenResponse(String accessToken, long expiresIn, String refreshToken, boolean mfaRequired) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.mfaRequired = mfaRequired;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isMfaRequired() {
        return mfaRequired;
    }
}
