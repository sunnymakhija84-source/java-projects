package com.sunny.ai.ingestion.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Kafka event published when a document has been chunked and is ready for embedding.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentIngestionEvent {

    private String documentId;
    private int totalChunks;

    @Builder.Default
    private Instant occurredAt = Instant.now();
}
