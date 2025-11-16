package com.codex.blog.common.dto;

import java.time.Instant;

/**
 * Standard API response wrapper to keep responses consistent across services.
 */
public class ApiResponse<T> {

    private final String code;
    private final String message;
    private final T data;
    private final String traceId;
    private final Instant timestamp;

    public ApiResponse(String code, String message, T data, String traceId, Instant timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getTraceId() {
        return traceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("OK", "", data, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data, String traceId) {
        return new ApiResponse<>("OK", "", data, traceId, Instant.now());
    }

    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(code, message, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> failure(String code, String message, String traceId) {
        return new ApiResponse<>(code, message, null, traceId, Instant.now());
    }
}
