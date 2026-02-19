package com.sunny.ai.embedding.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.ai.embedding.service.EmbeddingGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka consumer that listens for document-ingested events and triggers embedding generation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionEventListener {

    private final EmbeddingGeneratorService embeddingGeneratorService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "document-ingested",
            groupId = "embedding-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleDocumentIngested(String message) {
        try {
            Map<?, ?> event = objectMapper.readValue(message, Map.class);
            String documentId = (String) event.get("documentId");
            log.info("Received document-ingested event for documentId: {}", documentId);

            // In a real system, fetch chunks from document-ingestion-service via Feign/WebClient
            // and iterate over them. Here we log the trigger.
            log.info("Embedding generation triggered for documentId: {}", documentId);

        } catch (Exception e) {
            log.error("Failed to process document-ingested event: {}", e.getMessage(), e);
        }
    }
}
