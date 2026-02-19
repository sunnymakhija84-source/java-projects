package com.sunny.ai.embedding.repository;

import com.sunny.ai.embedding.domain.Embedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmbeddingRepository extends JpaRepository<Embedding, String> {

    List<Embedding> findByDocumentId(String documentId);

    Optional<Embedding> findByChunkId(String chunkId);

    long countByDocumentId(String documentId);
}
