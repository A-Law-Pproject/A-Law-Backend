package com.service.alaw.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;           // contract-analysis-ex

    @Value("${app.rabbitmq.queue}")
    private String queueName;             // contract-analysis-queue

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;            // contract.analyze

    @Value("${app.rabbitmq.ai-result-queue}")
    private String aiResultQueueName;     // ai.result.queue

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    // ─── Spring → FastAPI ────────────────────────────────────────────────────

    /** 계약서 분석 요청 Exchange */
    @Bean
    public DirectExchange contractExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    /** 계약서 분석 요청 Queue (DLQ 연결 + TTL 24h) */
    @Bean
    public Queue contractQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", exchangeName + ".dlx");
        args.put("x-dead-letter-routing-key", routingKey + ".failed");
        args.put("x-message-ttl", 86400000);
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    public Binding contractBinding() {
        return BindingBuilder.bind(contractQueue()).to(contractExchange()).with(routingKey);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(routingKey + ".failed");
    }

    // ─── FastAPI → Spring ────────────────────────────────────────────────────

    /** FastAPI가 분석 결과를 publish하는 Exchange */
    @Bean
    public DirectExchange aiResultExchange() {
        return new DirectExchange("contract.analysis.result", true, false);
    }

    /** Spring이 분석 결과를 consume하는 Queue (SSEListener가 구독) */
    @Bean
    public Queue aiResultQueue() {
        return new Queue(aiResultQueueName, true);  // ai.result.queue
    }

    @Bean
    public Binding aiResultBinding() {
        return BindingBuilder.bind(aiResultQueue()).to(aiResultExchange()).with("ai.result");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(exchangeName + ".dlx", true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(queueName + ".dlx").build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(routingKey + ".failed");
    }

}
