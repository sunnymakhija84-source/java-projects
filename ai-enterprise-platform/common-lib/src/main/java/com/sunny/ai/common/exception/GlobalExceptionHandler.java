package com.sunny.ai.common.exception;

import com.sunny.ai.common.dto.ApiResponse;
import com.sunny.ai.common.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler to be used as a base in service modules.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AIPlatformException.class)
    public ResponseEntity<ApiResponse<Void>> handleAIPlatformException(AIPlatformException ex) {
        ErrorDetails error = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResponse.failure(ex.getMessage(), error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing));

        ErrorDetails error = ErrorDetails.builder()
                .errorCode("VALIDATION_FAILED")
                .errorMessage("Request validation failed")
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Validation failed", error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        ErrorDetails error = ErrorDetails.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("An unexpected error occurred", error));
    }
}
