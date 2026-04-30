package com.service.alaw.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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
    private String exchangeName;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.ai-result-queue}")
    private String aiResultQueueName;

    @Value("${app.rabbitmq.voice-exchange}")
    private String voiceExchangeName;

    @Value("${app.rabbitmq.voice-queue}")
    private String voiceQueueName;

    @Value("${app.rabbitmq.voice-routing-key}")
    private String voiceRoutingKey;

    @Value("${app.rabbitmq.voice-result-queue}")
    private String voiceResultQueueName;

    @Value("${app.rabbitmq.voice-result-routing-key}")
    private String voiceResultRoutingKey;

    @Value("${app.rabbitmq.voice-result-exchange}")
    private String voiceResultExchangeName;

    private static final String DLX_SUFFIX = ".dlx";
    private static final String DLQ_SUFFIX = ".dlq";
    private static final String DLQ_ROUTING_KEY = "dead-letter";

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

    @Bean
    public DirectExchange contractExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue contractQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", exchangeName + DLX_SUFFIX);
        args.put("x-dead-letter-routing-key", routingKey + ".failed");
        args.put("x-message-ttl", 86400000);
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    public Binding contractBinding() {
        return BindingBuilder.bind(contractQueue()).to(contractExchange()).with(routingKey);
    }

    @Bean
    public DirectExchange contractDeadLetterExchange() {
        return new DirectExchange(exchangeName + DLX_SUFFIX, true, false);
    }

    @Bean
    public Queue contractDeadLetterQueue() {
        return QueueBuilder.durable(queueName + DLQ_SUFFIX).build();
    }

    @Bean
    public Binding contractDeadLetterBinding() {
        return BindingBuilder.bind(contractDeadLetterQueue())
                .to(contractDeadLetterExchange())
                .with(routingKey + ".failed");
    }

    @Bean
    public DirectExchange aiResultExchange() {
        return new DirectExchange("contract.analysis.result", true, false);
    }

    @Bean
    public Queue aiResultQueue() {
        return QueueBuilder.durable(aiResultQueueName)
                .withArgument("x-dead-letter-exchange", aiResultDeadLetterExchangeName())
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding aiResultBinding() {
        return BindingBuilder.bind(aiResultQueue()).to(aiResultExchange()).with("ai.result");
    }

    @Bean
    public DirectExchange aiResultDeadLetterExchange() {
        return new DirectExchange(aiResultDeadLetterExchangeName(), true, false);
    }

    @Bean
    public Queue aiResultDeadLetterQueue() {
        return QueueBuilder.durable(aiResultQueueName + DLQ_SUFFIX).build();
    }

    @Bean
    public Binding aiResultDeadLetterBinding() {
        return BindingBuilder.bind(aiResultDeadLetterQueue())
                .to(aiResultDeadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public DirectExchange voiceExchange() {
        return new DirectExchange(voiceExchangeName, true, false);
    }

    @Bean
    public Queue voiceQueue() {
        return new Queue(voiceQueueName, true);
    }

    @Bean
    public Binding voiceBinding() {
        return BindingBuilder.bind(voiceQueue()).to(voiceExchange()).with(voiceRoutingKey);
    }

    @Bean
    public DirectExchange voiceResultExchange() {
        return new DirectExchange(voiceResultExchangeName, true, false);
    }

    @Bean
    public Queue voiceResultQueue() {
        return new Queue(voiceResultQueueName, true, false, false);
    }

    @Bean
    public Binding voiceResultBinding() {
        return BindingBuilder.bind(voiceResultQueue())
                .to(voiceResultExchange())
                .with(voiceResultRoutingKey);
    }

    private String aiResultDeadLetterExchangeName() {
        return "contract.analysis.result" + DLX_SUFFIX;
    }
}
