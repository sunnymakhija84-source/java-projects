package com.sunny.ai.ingestion.domain;

public enum ChunkStatus {
    PENDING,
    EMBEDDING_QUEUED,
    EMBEDDED,
    FAILED
}
