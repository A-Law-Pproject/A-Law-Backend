package com.service.alaw.infra.messaging;

import com.service.alaw.infra.sse.SseEmitterManager;
import com.service.alaw.platform.contract.application.dto.analysis.AnalysisResultMessage;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.repository.ContractAnalysisDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractAnalysisConsumer {

    private final ContractAnalysisDocumentRepository analysisDocumentRepository;
    private final SseEmitterManager sseEmitterManager;

    @RabbitListener(queues = "${app.rabbitmq.result-queue}")
    public void handleAnalysisResult(AnalysisResultMessage message) {
        log.info("분석 결과 수신 - s3Key: {}, status: {}", message.s3Key(), message.status());

        try {
            if (message.isSuccess()) {
                // 1. MongoDB 저장
                ContractAnalysisDocument document = ContractAnalysisDocument.of(
                        null,
                        message.summary(),
                        message.riskScore(),
                        message.riskDetails()
                );
                analysisDocumentRepository.save(document);
                log.info("분석 결과 MongoDB 저장 완료 - s3Key: {}", message.s3Key());

                // 2. SSE 전송 (이벤트 타입을 "analysis-result"로 지정)
                sseEmitterManager.send(message.s3Key(), "analysis-result", message);
            } else {
                log.warn("분석 실패 메시지 수신 - s3Key: {}", message.s3Key());
                // SSE로 에러 이벤트 전송
                sseEmitterManager.send(message.s3Key(), "error", Map.of("message", "분석에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("분석 결과 처리 중 예외 발생 - s3Key: {}", message.s3Key(), e);
            // 시스템 예외 발생 시 SSE로 에러 알림
            sseEmitterManager.send(message.s3Key(), "error", Map.of("message", "결과 처리 중 서버 오류가 발생했습니다."));
        }
    }
}
