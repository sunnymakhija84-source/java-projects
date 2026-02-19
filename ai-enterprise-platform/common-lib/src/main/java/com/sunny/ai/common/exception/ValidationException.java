package com.sunny.ai.common.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AIPlatformException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST);
    }
}
