package com.service.alaw.infra.ai;

import com.service.alaw.common.exception.FastApiException;
import com.service.alaw.platform.chatbot.application.dto.ChatRequest;
import com.service.alaw.platform.chatbot.application.dto.ChatResponse;
import com.service.alaw.platform.contract.application.dto.explanation.EasyExplanationResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AIClient {

  private final WebClient webClient;
  private final int readTimeout;

  public AIClient(
      WebClient.Builder builder,
      @Value("${fastapi.base-url}") String baseUrl,
      @Value("${fastapi.timeout.read}") int readTimeout) {
    this.webClient = builder.baseUrl(baseUrl).build();
    this.readTimeout = readTimeout;
  }

  public EasyExplanationResponse getEasyExplanation(String term) {
    log.info("FastAPI 쉬운 말 요약 요청 - term: {}, 원문 길이: {}", term, term.length());

    try {
      EasyExplanationResponse response =
          webClient
              .post()
              .uri("/ai/contracts/explain/term")
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(Map.of("term", term))
              .retrieve()
              .onStatus(
                  HttpStatusCode::isError,
                  resp ->
                      resp.bodyToMono(String.class)
                          .flatMap(
                              body -> {
                                log.error("AI API 오류 응답: {}", body);
                                return Mono.error(new FastApiException("쉬운 말 요약 실패: " + body));
                              }))
              .bodyToMono(EasyExplanationResponse.class)
              .block(Duration.ofMillis(readTimeout));

      log.info("FastAPI 쉬운 말 요약 응답 완료");
      return response;

    } catch (Exception e) {
      log.error("FastAPI AI 호출 실패 - Error: {}", e.getMessage());
      throw new FastApiException("쉬운 말 요약 처리 중 오류 발생: " + e.getMessage(), e);
    }
  }

  public ChatResponse chat(ChatRequest request) {
    log.info("FastAPI 챗봇 요청 - sessionId: {}", request.sessionId());

    Map<String, Object> body = new HashMap<>();
    body.put("message", request.message());
    if (request.sessionId() != null) {
      body.put("session_id", request.sessionId());
    }

    try {
      ChatResponse response =
          webClient
              .post()
              .uri("/ai/chat")
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(body)
              .retrieve()
              .onStatus(
                  HttpStatusCode::isError,
                  resp ->
                      resp.bodyToMono(String.class)
                          .flatMap(
                              b -> {
                                log.error("챗봇 API 오류 응답: {}", b);
                                return Mono.error(new FastApiException("챗봇 응답 실패: " + b));
                              }))
              .bodyToMono(ChatResponse.class)
              .block(Duration.ofMillis(readTimeout));

      log.info("FastAPI 챗봇 응답 완료 - sessionId: {}, turnCount: {}",
          response.sessionId(), response.turnCount());
      return response;

    } catch (Exception e) {
      log.error("FastAPI 챗봇 호출 실패 - Error: {}", e.getMessage());
      throw new FastApiException("챗봇 처리 중 오류 발생: " + e.getMessage(), e);
    }
  }
}
