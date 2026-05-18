package com.service.alaw.platform.contract.application.service;

import com.service.alaw.infra.sse.SseEmitterManager;
import com.service.alaw.platform.contract.application.dto.analysis.AnalysisResultMessage;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.entity.ContractStatus;
import com.service.alaw.platform.contract.domain.repository.AnalysisJobRepository;
import com.service.alaw.platform.contract.domain.repository.ContractAnalysisDocumentRepository;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ContractAnalysisResultService {

    private final ContractRepository contractRepository;
    private final AnalysisJobRepository jobRepository;
    private final ContractAnalysisDocumentRepository analysisDocumentRepository;
    private final SseEmitterManager sseEmitterManager;

    public void handle(AnalysisResultMessage message) {
        if (message.isSuccess()) {
            handleSuccess(message);
        } else {
            handleFailure(message);
        }

        publishSse(message);
    }

    private void handleSuccess(AnalysisResultMessage message) {
        jobRepository.findByJobId(message.jobId()).ifPresent(job -> {
            job.complete(message.processingTimeMs());
            log.info("[ResultService] Job 완료 처리: jobId={}, processingTimeMs={}", message.jobId(), message.processingTimeMs());
        });

        contractRepository.findById(message.contractId()).ifPresent(contract -> {
            contract.saveAnalysisResult(
                    message.summary() != null ? message.summary().summaryText() : "",
                    message.riskAnalysis() != null ? message.riskAnalysis().riskCount() : 0
            );
            contract.updateAnalysisId(message.jobId());
            log.info("[ResultService] Contract 분석 완료: contractId={}", message.contractId());
        });

        analysisDocumentRepository.save(ContractAnalysisDocument.from(message));
        log.info("[ResultService] MongoDB 저장 완료: contractId={}, riskCount={}",
                message.contractId(),
                message.riskAnalysis() != null ? message.riskAnalysis().riskCount() : 0);
    }

    private void handleFailure(AnalysisResultMessage message) {
        jobRepository.findByJobId(message.jobId()).ifPresent(job -> {
            job.fail(message.errorMessage());
            log.warn("[ResultService] Job 실패 처리: jobId={}, error={}", message.jobId(), message.errorMessage());
        });

        contractRepository.findById(message.contractId()).ifPresent(contract ->
                contract.updateStatus(ContractStatus.FAILED)
        );
    }

    private void publishSse(AnalysisResultMessage message) {
        String jobId = message.jobId();

        if (message.isSuccess()) {
            if (message.summary() != null) {
                sseEmitterManager.send(jobId, "summary_result", Map.of(
                        "title", defaultString(message.summary().title()),
                        "summaryText", defaultString(message.summary().summaryText()),
                        "keyTerms", defaultList(message.summary().keyTerms())
                ));
            }

            if (message.riskAnalysis() != null) {
                sseEmitterManager.send(jobId, "analysis_result", Map.of(
                        "totalClauses", message.riskAnalysis().totalClauses(),
                        "riskCount", message.riskAnalysis().riskCount(),
                        "cautionCount", message.riskAnalysis().cautionCount(),
                        "safetyCount", message.riskAnalysis().safetyCount(),
                        "riskPercentage", message.riskAnalysis().riskPercentage(),
                        "clauseResults", defaultList(message.riskAnalysis().clauseResults())
                ));
            }
        } else {
            sseEmitterManager.send(jobId, "error", Map.of("message", "분석에 실패했습니다."));
        }

        sseEmitterManager.send(jobId, "analysis_complete", Map.of(
                "status", message.status() != null ? message.status() : "FAILED",
                "jobId", jobId,
                "processingTimeMs", message.processingTimeMs()
        ));
        sseEmitterManager.complete(jobId);
    }

    private String defaultString(String value) {
        return value != null ? value : "";
    }

    private <T> List<T> defaultList(List<T> value) {
        return value != null ? value : List.of();
    }
}
