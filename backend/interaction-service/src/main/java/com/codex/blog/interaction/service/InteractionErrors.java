package com.codex.blog.interaction.service;

import com.codex.blog.common.exception.error.ErrorCode;

public enum InteractionErrors implements ErrorCode {
    COMMENT_NOT_FOUND("COMMENT_404_NOT_FOUND", "Comment not found");

    private final String code;
    private final String message;

    InteractionErrors(String code, String message) {
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
