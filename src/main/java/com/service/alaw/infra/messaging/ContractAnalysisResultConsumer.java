package com.service.alaw.infra.messaging;

import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisResultMessage;
import com.service.alaw.platform.contract.application.service.ContractAnalysisResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractAnalysisResultConsumer {

    private final ContractAnalysisResultService resultService;

    @RabbitListener(queues = "${app.rabbitmq.result-queue}")
    public void consume(@Payload ContractAnalysisResultMessage message) {
        log.info("[ResultConsumer] 수신: jobId={}, status={}", message.jobId(), message.status());
        try {
            if ("COMPLETED".equals(message.status())) {
                resultService.handleSuccess(message);
            } else {
                resultService.handleFailure(message);
            }
        } catch (Exception e) {
            log.error("[ResultConsumer] 처리 실패: jobId={}", message.jobId(), e);
            throw new AmqpRejectAndDontRequeueException("처리 실패", e);
        }
    }
}
