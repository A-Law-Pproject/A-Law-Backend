package com.service.alaw.infra.config;

import com.service.alaw.common.exception.FastApiException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    @Value("${fastapi.timeout.connect}")
    private int connectTimeout;

    @Value("${fastapi.timeout.read}")
    private int readTimeout;

    @Bean
    public WebClient fastApiWebClient() {
        // Netty HTTP 클라이언트 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
                .baseUrl(fastApiBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())  // 요청 로깅
                .filter(logResponse()) // 응답 로깅
                .filter(errorHandler()) // 에러 핸들링
                .build();
    }

    // 요청 로깅
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("FastAPI Request: " + clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> System.out.println(name + ": " + value)));
            return Mono.just(clientRequest);
        });
    }

    // 응답 로깅
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.println("FastAPI Response Status: " + clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    // 에러 핸들링
    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is4xxClientError() ||
                    clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(
                                new FastApiException("FastAPI Error: " + errorBody)
                        ));
            }
            return Mono.just(clientResponse);
        });
    }
}
