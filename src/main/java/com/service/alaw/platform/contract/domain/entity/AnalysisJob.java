package com.service.alaw.platform.contract.domain.entity;

import com.service.alaw.platform.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "analysis_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisJob extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false, unique = true, length = 100)
    private String jobId;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnalysisJobStatus status;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Builder
    private AnalysisJob(String jobId, Long contractId) {
        this.jobId = jobId;
        this.contractId = contractId;
        this.status = AnalysisJobStatus.PENDING;
    }

    public static AnalysisJob of(String jobId, Long contractId) {
        return AnalysisJob.builder()
                .jobId(jobId)
                .contractId(contractId)
                .build();
    }

    public void complete(long processingTimeMs) {
        this.status = AnalysisJobStatus.COMPLETED;
        this.processingTimeMs = processingTimeMs;
    }

    public void fail(String errorMessage) {
        this.status = AnalysisJobStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}
