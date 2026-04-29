package com.service.alaw.platform.contract.presentation;

import com.service.alaw.infra.sse.SseEmitterManager;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.repository.ContractAnalysisDocumentRepository;
import com.service.alaw.platform.contract.presentation.swagger.ContractSseSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractSseController implements ContractSseSpec {

    private final SseEmitterManager sseEmitterManager;
    private final ContractAnalysisDocumentRepository analysisDocumentRepository;

    @GetMapping(value = "/analysis/{jobId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String jobId) {
        log.info("[SSE] Subscribe request - jobId={}", jobId);
        SseEmitter emitter = sseEmitterManager.register(jobId);
        replayStoredResult(jobId, emitter);
        return emitter;
    }

    private void replayStoredResult(String jobId, SseEmitter emitter) {
        analysisDocumentRepository.findByJobId(jobId).ifPresent(document -> {
            log.info("[SSE] Replaying stored analysis result - jobId={}", jobId);

            if (hasSummary(document)) {
                sseEmitterManager.send(emitter, "summary_result", Map.of(
                        "title", valueOrEmpty(document.getSummaryTitle()),
                        "summaryText", valueOrEmpty(document.getSummaryText()),
                        "keyTerms", document.getKeyTerms() != null ? document.getKeyTerms() : List.of()
                ));
            }

            if (hasAnalysis(document)) {
                sseEmitterManager.send(emitter, "analysis_result", Map.of(
                        "totalClauses", valueOrZero(document.getTotalClauses()),
                        "riskCount", valueOrZero(document.getRiskCount()),
                        "cautionCount", valueOrZero(document.getCautionCount()),
                        "safetyCount", valueOrZero(document.getSafetyCount()),
                        "riskPercentage", valueOrZero(document.getRiskPercentage()),
                        "clauseResults", document.getReplayClauseResults() != null
                                ? document.getReplayClauseResults() : List.of()
                ));
            }

            sseEmitterManager.send(emitter, "analysis_complete", Map.of(
                    "status", "COMPLETED",
                    "jobId", jobId,
                    "processingTimeMs", valueOrZero(document.getProcessingTimeMs())
            ));
            // 저장된 결과 리플레이 후 즉시 종료
            try { emitter.complete(); } catch (Exception ignored) {}
        });
    }

    private boolean hasSummary(ContractAnalysisDocument document) {
        return document.getSummaryTitle() != null
                || document.getSummaryText() != null
                || document.getKeyTerms() != null;
    }

    private boolean hasAnalysis(ContractAnalysisDocument document) {
        return document.getTotalClauses() != null
                || document.getRiskCount() != null
                || document.getCautionCount() != null
                || document.getSafetyCount() != null
                || document.getRiskPercentage() != null
                || document.getReplayClauseResults() != null;
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }

    private int valueOrZero(Integer value) {
        return value != null ? value : 0;
    }

    private double valueOrZero(Double value) {
        return value != null ? value : 0.0;
    }
}
