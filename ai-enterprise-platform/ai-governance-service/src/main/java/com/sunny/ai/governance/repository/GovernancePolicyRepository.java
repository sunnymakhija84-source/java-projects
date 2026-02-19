package com.sunny.ai.governance.repository;

import com.sunny.ai.governance.domain.GovernancePolicy;
import com.sunny.ai.governance.domain.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GovernancePolicyRepository extends JpaRepository<GovernancePolicy, String> {

    List<GovernancePolicy> findByStatus(PolicyStatus status);

    Optional<GovernancePolicy> findByName(String name);
}
