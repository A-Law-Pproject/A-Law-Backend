package com.service.alaw.platform.contract.domain.repository;

import com.service.alaw.platform.contract.domain.entity.AnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, Long> {
    Optional<AnalysisJob> findByJobId(String jobId);

    Optional<AnalysisJob> findByJobIdAndUserId(String jobId, Long userId);
}
