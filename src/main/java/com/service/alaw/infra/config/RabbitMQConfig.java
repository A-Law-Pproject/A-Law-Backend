package com.service.alaw.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

// RabbitMQConfig.java
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.result-queue}")
    private String resultQueueName;

    @Value("${app.rabbitmq.result-routing-key}")
    private String resultRoutingKey;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(messageConverter());
//        return rabbitTemplate;
//    }

    // Exchange: Direct (특정 큐로 라우팅)
    @Bean
    public DirectExchange contractExchange() {
        return new DirectExchange(exchangeName, true, false);
        // durable=true: 브로커 재시작해도 존재 유지
    }

    // Queue: Durable + 재시도 정책
    @Bean
    public Queue contractQueue() {
        Map<String, Object> args = new HashMap<>();
        // 재시도 실패 시 DLQ로 이동
        args.put("x-dead-letter-exchange", exchangeName + ".dlx");
        args.put("x-dead-letter-routing-key", routingKey + ".failed");
        // 메시지 최대 유지 시간: 24시간
        args.put("x-message-ttl", 86400000);

        return new Queue(queueName, true, false, false, args);
        // durable=true, exclusive=false, autoDelete=false
    }

    // Dead Letter Queue — 반복 실패 시 메시지 보관
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(queueName + ".dlq", true);
    }

    // Dead Letter Exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(exchangeName + ".dlx", true, false);
    }

    @Bean
    public Binding contractBinding() {
        return BindingBuilder
                .bind(contractQueue())
                .to(contractExchange())
                .with(routingKey);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(routingKey + ".failed");
    }

    // 분석 결과 수신용 큐
    @Bean
    public Queue resultQueue() {
        return new Queue(resultQueueName, true);
    }

    @Bean
    public Binding resultBinding() {
        return BindingBuilder
                .bind(resultQueue())
                .to(contractExchange())
                .with(resultRoutingKey);
    }
}
