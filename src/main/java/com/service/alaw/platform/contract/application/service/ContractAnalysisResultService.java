package com.service.alaw.platform.contract.application.service;

import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisResultMessage;
import com.service.alaw.platform.contract.domain.entity.ContractStatus;
import com.service.alaw.platform.contract.domain.repository.AnalysisJobRepository;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ContractAnalysisResultService {

    private final ContractRepository contractRepository;
    private final AnalysisJobRepository jobRepository;

    public void handleSuccess(ContractAnalysisResultMessage message) {
        // 1. Job 상태 COMPLETED로 업데이트
        jobRepository.findByJobId(message.jobId()).ifPresent(job -> {
            job.complete(message.processingTimeMs());
            log.info("[ResultService] Job 완료 처리: jobId={}, processingTimeMs={}", message.jobId(), message.processingTimeMs());
        });

        // 2. Contract 상태 COMPLETED로 업데이트
        contractRepository.findById(message.contractId()).ifPresent(contract -> {
            contract.saveAnalysisResult(
                    message.summary() != null ? message.summary().summaryText() : "",
                    message.riskAnalysis() != null ? message.riskAnalysis().riskCount() : 0
            );
            log.info("[ResultService] Contract 분석 완료: contractId={}", message.contractId());
        });
    }

    public void handleFailure(ContractAnalysisResultMessage message) {
        jobRepository.findByJobId(message.jobId()).ifPresent(job -> {
            job.fail(message.errorMessage());
            log.warn("[ResultService] Job 실패 처리: jobId={}, error={}", message.jobId(), message.errorMessage());
        });

        contractRepository.findById(message.contractId()).ifPresent(contract ->
                contract.updateStatus(ContractStatus.FAILED)
        );
    }
}
