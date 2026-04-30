package com.service.alaw.infra.messaging;

import com.service.alaw.platform.contract.application.dto.analysis.AnalysisResultMessage;
import com.service.alaw.platform.contract.application.service.ContractAnalysisResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractAnalysisConsumer {

    private final ContractAnalysisResultService resultService;

    @RabbitListener(queues = "${app.rabbitmq.ai-result-queue}")
    public void handleAnalysisResult(AnalysisResultMessage message) {
        log.info("[Consumer] 분석 결과 수신: jobId={}, contractId={}, status={}",
                message.jobId(), message.contractId(), message.status());
        resultService.handle(message);
    }
}
