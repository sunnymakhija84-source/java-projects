package com.sunny.ai.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Fallback controller returned when a downstream service is unavailable.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/document-ingestion")
    public ResponseEntity<Map<String, Object>> documentIngestionFallback() {
        return buildFallback("document-ingestion-service");
    }

    @GetMapping("/embedding")
    public ResponseEntity<Map<String, Object>> embeddingFallback() {
        return buildFallback("embedding-service");
    }

    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> queryFallback() {
        return buildFallback("query-service");
    }

    @GetMapping("/governance")
    public ResponseEntity<Map<String, Object>> governanceFallback() {
        return buildFallback("ai-governance-service");
    }

    private ResponseEntity<Map<String, Object>> buildFallback(String serviceName) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "message", serviceName + " is currently unavailable. Please try again later.",
                        "errorCode", "SERVICE_UNAVAILABLE"
                ));
    }
}
