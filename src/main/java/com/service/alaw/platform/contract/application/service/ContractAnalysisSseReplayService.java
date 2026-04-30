package com.service.alaw.platform.contract.application.service;

import com.service.alaw.infra.sse.SseEmitterManager;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.entity.AnalysisJob;
import com.service.alaw.platform.contract.domain.entity.AnalysisJobStatus;
import com.service.alaw.platform.contract.domain.repository.AnalysisJobRepository;
import com.service.alaw.platform.contract.domain.repository.ContractAnalysisDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractAnalysisSseReplayService {

    private final SseEmitterManager sseEmitterManager;
    private final ContractAnalysisDocumentRepository analysisDocumentRepository;
    private final AnalysisJobRepository analysisJobRepository;

    public void replayIfFinished(String jobId, SseEmitter emitter) {
        Optional<ContractAnalysisDocument> analysisDocument = analysisDocumentRepository.findByJobId(jobId);
        if (analysisDocument.isPresent()) {
            replayCompleted(jobId, emitter, analysisDocument.get());
            return;
        }

        analysisJobRepository.findByJobId(jobId)
                .filter(job -> job.getStatus() == AnalysisJobStatus.FAILED)
                .ifPresent(job -> replayFailure(jobId, emitter, job));
    }

    private void replayCompleted(String jobId, SseEmitter emitter, ContractAnalysisDocument document) {
        log.info("[SSE] Replaying completed analysis for jobId={}", jobId);

        sseEmitterManager.send(emitter, "summary_result", Map.of(
                "title", valueOrEmpty(document.getSummaryTitle()),
                "summaryText", valueOrEmpty(document.getSummaryText()),
                "keyTerms", safeList(document.getKeyTerms())
        ));

        sseEmitterManager.send(emitter, "analysis_result", Map.of(
                "totalClauses", valueOrZero(document.getTotalClauses()),
                "riskCount", valueOrZero(document.getRiskCount()),
                "cautionCount", valueOrZero(document.getCautionCount()),
                "safetyCount", valueOrZero(document.getSafetyCount()),
                "riskPercentage", valueOrZero(document.getRiskPercentage()),
                "clauseResults", safeList(document.getClauseResults())
        ));

        sseEmitterManager.send(emitter, "analysis_complete", Map.of(
                "status", AnalysisJobStatus.COMPLETED.name(),
                "jobId", jobId,
                "processingTimeMs", valueOrZero(document.getProcessingTimeMs())
        ));
        emitter.complete();
    }

    private void replayFailure(String jobId, SseEmitter emitter, AnalysisJob job) {
        log.info("[SSE] Replaying failed analysis for jobId={}", jobId);

        sseEmitterManager.send(emitter, "error", Map.of(
                "message", valueOrEmpty(job.getErrorMessage())
        ));
        sseEmitterManager.send(emitter, "analysis_complete", Map.of(
                "status", AnalysisJobStatus.FAILED.name(),
                "jobId", jobId,
                "processingTimeMs", valueOrZero(job.getProcessingTimeMs())
        ));
        emitter.complete();
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }

    private int valueOrZero(Integer value) {
        return value != null ? value : 0;
    }

    private long valueOrZero(Long value) {
        return value != null ? value : 0L;
    }

    private double valueOrZero(Double value) {
        return value != null ? value : 0.0;
    }

    private <T> List<T> safeList(List<T> value) {
        return value != null ? value : List.of();
    }
}
