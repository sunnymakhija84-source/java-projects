package com.sunny.ai.ingestion.controller;

import com.sunny.ai.common.dto.ApiResponse;
import com.sunny.ai.common.dto.PagedResponse;
import com.sunny.ai.ingestion.dto.DocumentIngestionRequest;
import com.sunny.ai.ingestion.dto.DocumentResponse;
import com.sunny.ai.ingestion.service.DocumentIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Ingestion", description = "APIs for uploading and managing documents")
public class DocumentController {

    private final DocumentIngestionService ingestionService;

    @PostMapping
    @Operation(summary = "Ingest a new document")
    public ResponseEntity<ApiResponse<DocumentResponse>> ingestDocument(
            @Valid @RequestBody DocumentIngestionRequest request) {
        DocumentResponse response = ingestionService.ingestDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Document ingestion started successfully"));
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Get a document by ID")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocument(@PathVariable String documentId) {
        return ResponseEntity.ok(ApiResponse.success(ingestionService.getDocument(documentId)));
    }

    @GetMapping
    @Operation(summary = "List all documents")
    public ResponseEntity<ApiResponse<PagedResponse<DocumentResponse>>> listDocuments(
            @RequestParam(required = false) String uploadedBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DocumentResponse> docs = ingestionService.listDocuments(
                uploadedBy, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        PagedResponse<DocumentResponse> paged = PagedResponse.<DocumentResponse>builder()
                .content(docs.getContent())
                .page(docs.getNumber())
                .size(docs.getSize())
                .totalElements(docs.getTotalElements())
                .totalPages(docs.getTotalPages())
                .first(docs.isFirst())
                .last(docs.isLast())
                .build();
        return ResponseEntity.ok(ApiResponse.success(paged));
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete a document")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable String documentId) {
        ingestionService.deleteDocument(documentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Document deleted successfully"));
    }
}
