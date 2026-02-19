package com.sunny.ai.governance.dto;

import com.sunny.ai.governance.domain.PolicyStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class GovernancePolicyResponse {
    private String id;
    private String name;
    private String description;
    private PolicyStatus status;
    private String rules;
    private String createdBy;
    private Instant createdAt;
}
