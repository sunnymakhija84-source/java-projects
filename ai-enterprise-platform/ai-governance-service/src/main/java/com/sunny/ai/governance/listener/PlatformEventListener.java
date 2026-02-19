package com.sunny.ai.governance.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.ai.governance.domain.RiskLevel;
import com.sunny.ai.governance.service.GovernanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Listens to platform-wide events and records them as governance audit logs.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PlatformEventListener {

    private final GovernanceService governanceService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "query-executed",
            groupId = "governance-service-group"
    )
    public void handleQueryExecuted(String message) {
        try {
            Map<?, ?> event = objectMapper.readValue(message, Map.class);
            governanceService.recordAuditEvent(
                    "QUERY_EXECUTED",
                    "query-service",
                    (String) event.getOrDefault("userId", "unknown"),
                    (String) event.getOrDefault("queryId", ""),
                    message,
                    RiskLevel.LOW,
                    false
            );
            log.debug("Recorded audit log for QUERY_EXECUTED event");
        } catch (Exception e) {
            log.error("Failed to process query-executed event for audit: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "document-ingested",
            groupId = "governance-service-group"
    )
    public void handleDocumentIngested(String message) {
        try {
            Map<?, ?> event = objectMapper.readValue(message, Map.class);
            governanceService.recordAuditEvent(
                    "DOCUMENT_INGESTED",
                    "document-ingestion-service",
                    "system",
                    (String) event.getOrDefault("documentId", ""),
                    message,
                    RiskLevel.LOW,
                    false
            );
            log.debug("Recorded audit log for DOCUMENT_INGESTED event");
        } catch (Exception e) {
            log.error("Failed to process document-ingested event for audit: {}", e.getMessage(), e);
        }
    }
}
