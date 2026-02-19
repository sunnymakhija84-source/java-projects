package com.sunny.ai.common.constants;

/**
 * Platform-wide constants.
 */
public final class PlatformConstants {

    private PlatformConstants() {
    }

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String API_VERSION_HEADER = "X-API-Version";

    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final int MAX_PAGE_SIZE = 100;

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    // Service names
    public static final String SERVICE_DOCUMENT_INGESTION = "document-ingestion-service";
    public static final String SERVICE_EMBEDDING = "embedding-service";
    public static final String SERVICE_QUERY = "query-service";
    public static final String SERVICE_GOVERNANCE = "ai-governance-service";
    public static final String SERVICE_API_GATEWAY = "api-gateway";

    // Event types
    public static final String EVENT_DOCUMENT_UPLOADED = "DOCUMENT_UPLOADED";
    public static final String EVENT_EMBEDDING_GENERATED = "EMBEDDING_GENERATED";
    public static final String EVENT_QUERY_EXECUTED = "QUERY_EXECUTED";
}
