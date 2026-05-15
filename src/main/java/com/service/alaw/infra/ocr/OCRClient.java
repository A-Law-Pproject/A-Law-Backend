package com.service.alaw.infra.ocr;

import com.service.alaw.common.exception.FastApiException;
import com.service.alaw.platform.contract.application.dto.ocr.FastApiOcrResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class OCRClient {

    private final WebClient webClient;

    public OCRClient(@Qualifier("fastApiOcrWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * FastAPI /ocr에 동기 호출.
     * s3_key를 전달하면 FastAPI가 S3에서 이미지를 가져와 OCR 실행.
     */
    public FastApiOcrResponse callOCR(String s3Key) {
        log.info("FastAPI OCR 요청 - S3 Key: {}", s3Key);

        try {
            FastApiOcrResponse response = webClient.post()
                    .uri("/ai/contracts/ocr")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("s3_key", s3Key))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("OCR API 오류 응답: {}", body);
                                        return Mono.error(new FastApiException("OCR 실패: " + body));
                                    })
                    )
                    .bodyToMono(FastApiOcrResponse.class)
                    .block(Duration.ofSeconds(90));

            log.info("FastAPI OCR 응답 수신 - success: {}, 단어 수: {}",
                    response.success(),
                    response.words() != null ? response.words().size() : 0);


            return response;

        } catch (Exception e) {
            log.error("FastAPI OCR 호출 실패 - S3 Key: {}, Error: {}", s3Key, e.getMessage());
            throw new FastApiException("OCR 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
