package com.sunny.ai.ingestion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request payload for ingesting a new document from a URL or text.
 */
@Data
public class DocumentIngestionRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String sourceUrl;

    private String rawText;

    private String uploadedBy;
}
