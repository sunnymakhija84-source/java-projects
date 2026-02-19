package com.sunny.ai.embedding.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Stores the vector embedding for a document chunk.
 */
@Entity
@Table(name = "embeddings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Embedding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String chunkId;

    @Column(nullable = false)
    private String documentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String chunkContent;

    /** Stored as comma-separated floats; in production use pgvector column type */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String vectorData;

    private String modelName;

    private Integer dimensions;

    @CreationTimestamp
    private Instant createdAt;
}
