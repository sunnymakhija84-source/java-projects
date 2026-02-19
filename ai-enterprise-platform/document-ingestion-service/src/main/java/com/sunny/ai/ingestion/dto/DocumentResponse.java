package com.sunny.ai.ingestion.dto;

import com.sunny.ai.ingestion.domain.DocumentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for document queries.
 */
@Data
@Builder
public class DocumentResponse {

    private String id;
    private String title;
    private String fileName;
    private String contentType;
    private Long fileSizeBytes;
    private DocumentStatus status;
    private String uploadedBy;
    private long chunkCount;
    private Instant createdAt;
    private Instant updatedAt;
}
