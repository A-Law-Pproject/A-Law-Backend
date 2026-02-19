package com.service.alaw.infra.ai;

import com.service.alaw.common.exception.FastApiException;
import com.service.alaw.platform.contract.application.dto.image.TextToImageResponse;
import java.time.Duration;
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
public class TextToImageClient {

  private final WebClient webClient;
  private final int readTimeout;

  public TextToImageClient(
      WebClient.Builder builder,
      @Value("${fastapi.base-url}") String baseUrl,
      @Value("${fastapi.timeout.read}") int readTimeout) {
    this.webClient = builder.baseUrl(baseUrl).build();
    this.readTimeout = readTimeout;
  }

  public TextToImageResponse convertTextToImage(Long contractId, String textContent) {
    log.info("FastAPI 텍스트→이미지 변환 요청 - contractId: {}, 텍스트 길이: {}", contractId, textContent.length());

    try {
      TextToImageResponse response =
          webClient
              .post()
              .uri("/contracts/{contractId}/image", contractId)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(Map.of("text_content", textContent))
              .retrieve()
              .onStatus(
                  HttpStatusCode::isError,
                  resp ->
                      resp.bodyToMono(String.class)
                          .flatMap(
                              body -> {
                                log.error("텍스트→이미지 변환 API 오류 응답: {}", body);
                                return Mono.error(new FastApiException("텍스트→이미지 변환 실패: " + body));
                              }))
              .bodyToMono(TextToImageResponse.class)
              .block(Duration.ofMillis(readTimeout));

      log.info("FastAPI 텍스트→이미지 변환 완료 - contractId: {}", contractId);
      return response;

    } catch (Exception e) {
      log.error("FastAPI 텍스트→이미지 변환 호출 실패 - Error: {}", e.getMessage());
      throw new FastApiException("텍스트→이미지 변환 처리 중 오류 발생: " + e.getMessage(), e);
    }
  }
}
