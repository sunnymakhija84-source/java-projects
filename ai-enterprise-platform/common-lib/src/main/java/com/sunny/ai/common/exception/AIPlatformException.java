package com.sunny.ai.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all AI Platform services.
 */
@Getter
public class AIPlatformException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public AIPlatformException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public AIPlatformException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
