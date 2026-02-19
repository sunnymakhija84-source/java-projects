package com.sunny.ai.ingestion.domain;

/**
 * Lifecycle states for a document in the ingestion pipeline.
 */
public enum DocumentStatus {
    UPLOADED,       // File received, not yet processed
    PROCESSING,     // Text extraction and chunking in progress
    CHUNKED,        // Split into chunks, ready for embedding
    EMBEDDED,       // Embeddings generated and stored
    FAILED,         // Processing error
    ARCHIVED        // Removed from active index
}
