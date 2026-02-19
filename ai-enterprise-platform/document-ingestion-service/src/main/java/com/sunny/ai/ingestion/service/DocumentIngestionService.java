package com.sunny.ai.ingestion.service;

import com.sunny.ai.common.exception.ResourceNotFoundException;
import com.sunny.ai.ingestion.domain.*;
import com.sunny.ai.ingestion.dto.DocumentIngestionRequest;
import com.sunny.ai.ingestion.dto.DocumentResponse;
import com.sunny.ai.ingestion.event.DocumentIngestionEvent;
import com.sunny.ai.ingestion.repository.DocumentChunkRepository;
import com.sunny.ai.ingestion.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionService {

    private static final int CHUNK_SIZE = 500; // characters per chunk
    private static final String TOPIC_DOCUMENT_INGESTED = "document-ingested";

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final KafkaTemplate<String, DocumentIngestionEvent> kafkaTemplate;

    @Transactional
    public DocumentResponse ingestDocument(DocumentIngestionRequest request) {
        log.info("Starting ingestion for document: {}", request.getTitle());

        Document document = Document.builder()
                .title(request.getTitle())
                .fileName(request.getTitle().replaceAll("\\s+", "_") + ".txt")
                .contentType("text/plain")
                .rawContent(request.getRawText())
                .sourceUrl(request.getSourceUrl())
                .uploadedBy(request.getUploadedBy())
                .status(DocumentStatus.UPLOADED)
                .build();

        document = documentRepository.save(document);
        log.info("Document saved with id: {}", document.getId());

        processDocumentAsync(document);

        return toResponse(document, 0L);
    }

    @Async
    @Transactional
    public void processDocumentAsync(Document document) {
        try {
            document.setStatus(DocumentStatus.PROCESSING);
            documentRepository.save(document);

            List<DocumentChunk> chunks = chunkText(document, document.getRawContent());
            chunkRepository.saveAll(chunks);

            document.setStatus(DocumentStatus.CHUNKED);
            documentRepository.save(document);

            // Publish Kafka event to trigger embedding generation
            DocumentIngestionEvent event = DocumentIngestionEvent.builder()
                    .documentId(document.getId())
                    .totalChunks(chunks.size())
                    .build();
            kafkaTemplate.send(TOPIC_DOCUMENT_INGESTED, document.getId(), event);

            log.info("Document {} chunked into {} parts and event published", document.getId(), chunks.size());
        } catch (Exception e) {
            log.error("Failed to process document {}: {}", document.getId(), e.getMessage(), e);
            document.setStatus(DocumentStatus.FAILED);
            documentRepository.save(document);
        }
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocument(String documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));
        long chunkCount = chunkRepository.countByDocumentId(documentId);
        return toResponse(document, chunkCount);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> listDocuments(String uploadedBy, Pageable pageable) {
        Page<Document> docs = (uploadedBy != null)
                ? documentRepository.findByUploadedBy(uploadedBy, pageable)
                : documentRepository.findAll(pageable);
        return docs.map(d -> toResponse(d, chunkRepository.countByDocumentId(d.getId())));
    }

    @Transactional
    public void deleteDocument(String documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));
        documentRepository.delete(document);
        log.info("Document {} deleted", documentId);
    }

    private List<DocumentChunk> chunkText(Document document, String text) {
        List<DocumentChunk> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) return chunks;

        int index = 0;
        int chunkIndex = 0;
        while (index < text.length()) {
            int end = Math.min(index + CHUNK_SIZE, text.length());
            String chunkContent = text.substring(index, end).trim();
            if (!chunkContent.isBlank()) {
                chunks.add(DocumentChunk.builder()
                        .document(document)
                        .chunkIndex(chunkIndex++)
                        .content(chunkContent)
                        .tokenCount(chunkContent.split("\\s+").length)
                        .status(ChunkStatus.PENDING)
                        .build());
            }
            index += CHUNK_SIZE;
        }
        return chunks;
    }

    private DocumentResponse toResponse(Document document, long chunkCount) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .fileName(document.getFileName())
                .contentType(document.getContentType())
                .fileSizeBytes(document.getFileSizeBytes())
                .status(document.getStatus())
                .uploadedBy(document.getUploadedBy())
                .chunkCount(chunkCount)
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
