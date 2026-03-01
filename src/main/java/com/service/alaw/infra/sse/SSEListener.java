package com.service.alaw.infra.sse;

import com.service.alaw.platform.contract.application.dto.analysis.AnalysisResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SSEListener {

    private final SseEmitterManager sseEmitterManager;

    /**
     * FastAPI에서 분석 결과 수신
     */
    @RabbitListener(queues = "ai.result.queue")
    public void handleAnalysisResult(AnalysisResultMessage result) {
        log.info("[Listener] Received analysis result: taskId={}", result.s3Key());

        String s3Key = result.s3Key();

        // 요약 결과 전송
        if (result.summary() != null) {
            sseEmitterManager.send(s3Key, "summary_result", Map.of(
                    "summary", result.summary(),
                    "s3Key", s3Key
            ));
        }

        // Risk 분석 결과 전송
        if (result.riskDetails() != null) {
            sseEmitterManager.send(s3Key, "analysis_result", Map.of(
                    "analysis", result.riskDetails(),
                    "s3Key", s3Key
            ));
        }

        // 완료 이벤트 전송
        sseEmitterManager.send(s3Key, "analysis_complete", Map.of(
                "status", "completed",
                "s3Key", s3Key
        ));
    }
}
