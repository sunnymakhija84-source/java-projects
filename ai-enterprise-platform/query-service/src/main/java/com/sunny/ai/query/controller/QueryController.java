package com.sunny.ai.query.controller;

import com.sunny.ai.common.dto.ApiResponse;
import com.sunny.ai.query.dto.QueryRequest;
import com.sunny.ai.query.dto.QueryResponse;
import com.sunny.ai.query.service.QueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queries")
@RequiredArgsConstructor
@Tag(name = "Query", description = "RAG-based question-answering APIs")
public class QueryController {

    private final QueryService queryService;

    @PostMapping
    @Operation(summary = "Execute a RAG query and get an AI-generated answer")
    public ResponseEntity<ApiResponse<QueryResponse>> query(
            @Valid @RequestBody QueryRequest request) {
        QueryResponse response = queryService.executeQuery(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
