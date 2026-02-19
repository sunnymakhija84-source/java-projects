package com.sunny.ai.governance.repository;

import com.sunny.ai.governance.domain.AuditLog;
import com.sunny.ai.governance.domain.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    Page<AuditLog> findByUserId(String userId, Pageable pageable);

    Page<AuditLog> findByEventType(String eventType, Pageable pageable);

    Page<AuditLog> findByRiskLevelAndCreatedAtBetween(
            RiskLevel riskLevel, Instant from, Instant to, Pageable pageable);

    long countByPolicyViolationTrue();
}
