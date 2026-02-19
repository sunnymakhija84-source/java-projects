package com.sunny.ai.embedding.controller;

import com.sunny.ai.common.dto.ApiResponse;
import com.sunny.ai.embedding.domain.Embedding;
import com.sunny.ai.embedding.service.EmbeddingGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/embeddings")
@RequiredArgsConstructor
@Tag(name = "Embedding", description = "APIs for embedding generation and retrieval")
public class EmbeddingController {

    private final EmbeddingGeneratorService embeddingGeneratorService;

    @GetMapping("/document/{documentId}")
    @Operation(summary = "Get all embeddings for a document")
    public ResponseEntity<ApiResponse<List<Embedding>>> getEmbeddingsByDocument(
            @PathVariable String documentId) {
        List<Embedding> embeddings = embeddingGeneratorService.getEmbeddingsForDocument(documentId);
        return ResponseEntity.ok(ApiResponse.success(embeddings));
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate embedding for a specific chunk")
    public ResponseEntity<ApiResponse<Embedding>> generateEmbedding(
            @RequestParam String chunkId,
            @RequestParam String documentId,
            @RequestBody String chunkContent) {
        Embedding embedding = embeddingGeneratorService.generateAndStore(chunkId, documentId, chunkContent);
        return ResponseEntity.ok(ApiResponse.success(embedding, "Embedding generated successfully"));
    }
}
