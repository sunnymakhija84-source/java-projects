package com.sunny.ai.query.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class QueryResponse {

    private String queryId;
    private String question;
    private String answer;
    private List<String> sourceChunkIds;
    private String modelUsed;
    private Long responseTimeMs;
    private Instant timestamp;
}
