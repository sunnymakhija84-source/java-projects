package com.sunny.ai.governance.service;

import com.sunny.ai.common.exception.ResourceNotFoundException;
import com.sunny.ai.governance.domain.*;
import com.sunny.ai.governance.dto.AuditLogResponse;
import com.sunny.ai.governance.dto.GovernancePolicyRequest;
import com.sunny.ai.governance.dto.GovernancePolicyResponse;
import com.sunny.ai.governance.repository.AuditLogRepository;
import com.sunny.ai.governance.repository.GovernancePolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GovernanceService {

    private final AuditLogRepository auditLogRepository;
    private final GovernancePolicyRepository policyRepository;

    // ---- Audit Logs ----

    public AuditLog recordAuditEvent(String eventType, String serviceName,
                                      String userId, String resourceId,
                                      String details, RiskLevel riskLevel, boolean violation) {
        AuditLog log = AuditLog.builder()
                .eventType(eventType)
                .serviceName(serviceName)
                .userId(userId)
                .resourceId(resourceId)
                .details(details)
                .riskLevel(riskLevel)
                .policyViolation(violation)
                .build();
        return auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(String userId, Pageable pageable) {
        Page<AuditLog> logs = (userId != null)
                ? auditLogRepository.findByUserId(userId, pageable)
                : auditLogRepository.findAll(pageable);
        return logs.map(this::toAuditResponse);
    }

    @Transactional(readOnly = true)
    public long countViolations() {
        return auditLogRepository.countByPolicyViolationTrue();
    }

    // ---- Policies ----

    @Transactional
    public GovernancePolicyResponse createPolicy(GovernancePolicyRequest request) {
        GovernancePolicy policy = GovernancePolicy.builder()
                .name(request.getName())
                .description(request.getDescription())
                .rules(request.getRules())
                .createdBy(request.getCreatedBy())
                .status(PolicyStatus.DRAFT)
                .build();
        policy = policyRepository.save(policy);
        log.info("Created governance policy: {}", policy.getName());
        return toPolicyResponse(policy);
    }

    @Transactional
    public GovernancePolicyResponse activatePolicy(String policyId) {
        GovernancePolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("GovernancePolicy", policyId));
        policy.setStatus(PolicyStatus.ACTIVE);
        return toPolicyResponse(policyRepository.save(policy));
    }

    @Transactional(readOnly = true)
    public List<GovernancePolicyResponse> getActivePolicies() {
        return policyRepository.findByStatus(PolicyStatus.ACTIVE)
                .stream().map(this::toPolicyResponse).toList();
    }

    @Transactional(readOnly = true)
    public GovernancePolicyResponse getPolicy(String policyId) {
        GovernancePolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("GovernancePolicy", policyId));
        return toPolicyResponse(policy);
    }

    // ---- Mappers ----

    private AuditLogResponse toAuditResponse(AuditLog al) {
        return AuditLogResponse.builder()
                .id(al.getId())
                .eventType(al.getEventType())
                .serviceName(al.getServiceName())
                .userId(al.getUserId())
                .resourceId(al.getResourceId())
                .riskLevel(al.getRiskLevel())
                .policyViolation(al.isPolicyViolation())
                .createdAt(al.getCreatedAt())
                .build();
    }

    private GovernancePolicyResponse toPolicyResponse(GovernancePolicy p) {
        return GovernancePolicyResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .status(p.getStatus())
                .rules(p.getRules())
                .createdBy(p.getCreatedBy())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
