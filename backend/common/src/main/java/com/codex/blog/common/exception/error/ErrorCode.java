package com.codex.blog.common.exception.error;

/**
 * Defines the structure of error codes returned to clients.
 */
public interface ErrorCode {

    String code();

    String message();
}
