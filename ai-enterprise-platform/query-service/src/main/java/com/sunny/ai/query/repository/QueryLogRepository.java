package com.sunny.ai.query.repository;

import com.sunny.ai.query.domain.QueryLog;
import com.sunny.ai.query.domain.QueryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLog, String> {

    Page<QueryLog> findByUserId(String userId, Pageable pageable);

    long countByStatus(QueryStatus status);
}
