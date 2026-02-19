package com.sunny.ai.ingestion.repository;

import com.sunny.ai.ingestion.domain.DocumentChunk;
import com.sunny.ai.ingestion.domain.ChunkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, String> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndex(String documentId);

    List<DocumentChunk> findByStatus(ChunkStatus status);

    long countByDocumentId(String documentId);
}
