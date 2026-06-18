package com.service.alaw.infra.messaging;

import com.service.alaw.platform.voice.application.dto.VoiceAnalysisMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceRecordPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.voice-exchange}")
    private String exchange;

    @Value("${app.rabbitmq.voice-routing-key}")
    private String routingKey;

    public void publish(VoiceAnalysisMessage message) {
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                message,
                msg -> {
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    msg.getMessageProperties().setCorrelationId(message.jobId());
                    return msg;
                }
        );
        log.info("[VoicePublisher] 발행 완료 - voiceRecordId={}, jobId={}, contractId={}",
                message.voiceRecordId(), message.jobId(), message.contractId());
    }
}
