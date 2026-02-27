package com.service.alaw.infra.messaging;

import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractAnalysisPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    public void publish(ContractAnalysisMessage message) {
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
    }
}
