package com.sunny.ai.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AIPlatformException {

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
                "RESOURCE_NOT_FOUND",
                String.format("%s not found with identifier: %s", resourceType, identifier),
                HttpStatus.NOT_FOUND);
    }
}
