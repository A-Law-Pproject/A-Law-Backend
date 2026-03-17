package com.service.alaw.infra.messaging;

import com.service.alaw.infra.sse.SseEmitterManager;
import com.service.alaw.platform.contract.application.dto.analysis.AnalysisResultMessage;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.repository.ContractAnalysisDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractAnalysisConsumer {

    private final ContractAnalysisDocumentRepository analysisDocumentRepository;
    private final SseEmitterManager sseEmitterManager;

    @RabbitListener(queues = "${app.rabbitmq.ai-result-queue}")
    public void handleAnalysisResult(AnalysisResultMessage message) {
        String jobId = message.jobId();
        log.info("[Consumer] 분석 결과 수신: jobId={}, contractId={}, status={}",
                jobId, message.contractId(), message.status());

        try {
            if (message.isSuccess()) {
                // 1. MongoDB 저장
                analysisDocumentRepository.save(ContractAnalysisDocument.from(message));
                log.info("[Consumer] MongoDB 저장 완료 - contractId={}, riskCount={}",
                        message.contractId(),
                        message.riskAnalysis() != null ? message.riskAnalysis().riskCount() : 0);

                // 2. SSE: summary_result
                if (message.summary() != null) {
                    sseEmitterManager.send(jobId, "summary_result", Map.of(
                            "title", message.summary().title() != null ? message.summary().title() : "",
                            "summaryText", message.summary().summaryText() != null ? message.summary().summaryText() : "",
                            "keyTerms", message.summary().keyTerms() != null ? message.summary().keyTerms() : List.of()
                    ));
                }

                // 3. SSE: analysis_result
                if (message.riskAnalysis() != null) {
                    sseEmitterManager.send(jobId, "analysis_result", Map.of(
                            "totalClauses", message.riskAnalysis().totalClauses(),
                            "riskCount", message.riskAnalysis().riskCount(),
                            "cautionCount", message.riskAnalysis().cautionCount(),
                            "safetyCount", message.riskAnalysis().safetyCount(),
                            "riskPercentage", message.riskAnalysis().riskPercentage(),
                            "clauseResults", message.riskAnalysis().clauseResults() != null
                                    ? message.riskAnalysis().clauseResults() : List.of()
                    ));
                }
            } else {
                log.warn("[Consumer] 분석 실패: jobId={}, error={}", jobId, message.errorMessage());
                sseEmitterManager.send(jobId, "error", Map.of("message", "분석에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("[Consumer] 분석 결과 처리 중 예외 발생 - jobId={}", jobId, e);
            sseEmitterManager.send(jobId, "error", Map.of("message", "결과 처리 중 서버 오류가 발생했습니다."));
        }

        // 항상 complete 이벤트 전송
        sseEmitterManager.send(jobId, "analysis_complete", Map.of(
                "status", message.status() != null ? message.status() : "FAILED",
                "jobId", jobId,
                "processingTimeMs", message.processingTimeMs()
        ));
    }
}
