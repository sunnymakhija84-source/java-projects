package com.sunny.ai.governance.controller;

import com.sunny.ai.common.dto.ApiResponse;
import com.sunny.ai.common.dto.PagedResponse;
import com.sunny.ai.governance.dto.AuditLogResponse;
import com.sunny.ai.governance.dto.GovernancePolicyRequest;
import com.sunny.ai.governance.dto.GovernancePolicyResponse;
import com.sunny.ai.governance.service.GovernanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/governance")
@RequiredArgsConstructor
@Tag(name = "AI Governance", description = "APIs for AI audit logs and policy management")
public class GovernanceController {

    private final GovernanceService governanceService;

    // ---- Audit Logs ----

    @GetMapping("/audit-logs")
    @Operation(summary = "List audit logs")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogResponse>>> getAuditLogs(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditLogResponse> logs = governanceService.getAuditLogs(
                userId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        PagedResponse<AuditLogResponse> paged = PagedResponse.<AuditLogResponse>builder()
                .content(logs.getContent())
                .page(logs.getNumber()).size(logs.getSize())
                .totalElements(logs.getTotalElements())
                .totalPages(logs.getTotalPages())
                .first(logs.isFirst()).last(logs.isLast())
                .build();
        return ResponseEntity.ok(ApiResponse.success(paged));
    }

    @GetMapping("/audit-logs/violations/count")
    @Operation(summary = "Count total policy violations")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countViolations() {
        long count = governanceService.countViolations();
        return ResponseEntity.ok(ApiResponse.success(Map.of("totalViolations", count)));
    }

    // ---- Policies ----

    @PostMapping("/policies")
    @Operation(summary = "Create a governance policy")
    public ResponseEntity<ApiResponse<GovernancePolicyResponse>> createPolicy(
            @Valid @RequestBody GovernancePolicyRequest request) {
        GovernancePolicyResponse response = governanceService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Policy created successfully"));
    }

    @PatchMapping("/policies/{policyId}/activate")
    @Operation(summary = "Activate a governance policy")
    public ResponseEntity<ApiResponse<GovernancePolicyResponse>> activatePolicy(
            @PathVariable String policyId) {
        GovernancePolicyResponse response = governanceService.activatePolicy(policyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Policy activated"));
    }

    @GetMapping("/policies/active")
    @Operation(summary = "List all active policies")
    public ResponseEntity<ApiResponse<List<GovernancePolicyResponse>>> getActivePolicies() {
        return ResponseEntity.ok(ApiResponse.success(governanceService.getActivePolicies()));
    }

    @GetMapping("/policies/{policyId}")
    @Operation(summary = "Get a policy by ID")
    public ResponseEntity<ApiResponse<GovernancePolicyResponse>> getPolicy(
            @PathVariable String policyId) {
        return ResponseEntity.ok(ApiResponse.success(governanceService.getPolicy(policyId)));
    }
}
