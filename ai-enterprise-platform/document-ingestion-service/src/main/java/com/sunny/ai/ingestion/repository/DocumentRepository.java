package com.sunny.ai.ingestion.repository;

import com.sunny.ai.ingestion.domain.Document;
import com.sunny.ai.ingestion.domain.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    Page<Document> findByUploadedBy(String uploadedBy, Pageable pageable);

    List<Document> findByStatus(DocumentStatus status);

    long countByStatus(DocumentStatus status);
}
