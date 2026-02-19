package com.sunny.ai.query.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Persisted record of a user query and its generated answer.
 */
@Entity
@Table(name = "query_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String retrievedContext;

    private String modelUsed;

    private String userId;

    private Long responseTimeMs;

    @Enumerated(EnumType.STRING)
    private QueryStatus status;

    @CreationTimestamp
    private Instant createdAt;
}
