package com.codex.blog.content.service;

import com.codex.blog.common.exception.error.ErrorCode;

public enum PostErrors implements ErrorCode {
    POST_NOT_FOUND("POST_404_NOT_FOUND", "Post not found");

    private final String code;
    private final String message;

    PostErrors(String code, String message) {
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
