package com.sunny.ai.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Error details embedded in ApiResponse failures.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

    private String errorCode;
    private String errorMessage;
    private Map<String, String> fieldErrors;
    private String details;
}
