package com.codex.blog.auth.service;

import com.codex.blog.common.exception.error.ErrorCode;

public enum AuthErrors implements ErrorCode {
    EMAIL_EXISTS("AUTH_409_EMAIL_EXISTS", "Email already exists"),
    INVALID_CREDENTIALS("AUTH_401_INVALID_CREDENTIALS", "Invalid credentials"),
    INVALID_REFRESH_TOKEN("AUTH_401_INVALID_REFRESH_TOKEN", "Invalid refresh token"),
    USER_NOT_FOUND("AUTH_404_USER_NOT_FOUND", "User not found");

    private final String code;
    private final String message;

    AuthErrors(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
