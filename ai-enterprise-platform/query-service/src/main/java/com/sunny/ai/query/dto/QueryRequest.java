package com.sunny.ai.query.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueryRequest {

    @NotBlank(message = "Question is required")
    private String question;

    private String userId;

    /** Number of top relevant chunks to retrieve */
    private int topK = 5;
}
