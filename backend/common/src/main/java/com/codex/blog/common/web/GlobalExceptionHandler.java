package com.codex.blog.common.web;

import com.codex.blog.common.dto.ApiResponse;
import com.codex.blog.common.exception.BusinessException;
import com.codex.blog.common.exception.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusiness(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(errorCode.code(), ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Object>> handleValidation(Exception ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleOther(Exception ex) {
        String traceId = UUID.randomUUID().toString();
        log.error("Unhandled error traceId={}", traceId, ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("INTERNAL_ERROR", "Internal server error", traceId));
    }
}
