package com.sunny.ai.governance.dto;

import com.sunny.ai.governance.domain.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuditLogResponse {
    private String id;
    private String eventType;
    private String serviceName;
    private String userId;
    private String resourceId;
    private RiskLevel riskLevel;
    private boolean policyViolation;
    private Instant createdAt;
}
