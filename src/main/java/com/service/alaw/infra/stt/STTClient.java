package com.service.alaw.infra.stt;

import com.service.alaw.common.exception.SttException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class STTClient {

    private final WebClient webClient;
    private final String apiKey;
    private final int timeout;

    public STTClient(
            WebClient.Builder builder,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.whisper.timeout}") int timeout) {
        this.webClient = builder.baseUrl("https://api.openai.com").build();
        this.apiKey = apiKey;
        this.timeout = timeout;
    }

    public String transcribe(byte[] audioBytes, String filename, String contentType) {
        log.info("[STTClient] Whisper API 요청 - filename={}, size={}bytes", filename, audioBytes.length);

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        }).contentType(MediaType.parseMediaType(contentType));
        bodyBuilder.part("model", "whisper-1");
        bodyBuilder.part("language", "ko");

        try {
            WhisperResponse response = webClient.post()
                    .uri("/v1/audio/transcriptions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("[STTClient] Whisper API 오류: {}", body);
                                        return Mono.error(new SttException("STT 변환 실패: " + body));
                                    })
                    )
                    .bodyToMono(WhisperResponse.class)
                    .block(Duration.ofMillis(timeout));

            log.info("[STTClient] Whisper API 응답 완료 - transcript 길이={}",
                    response != null && response.text() != null ? response.text().length() : 0);
            return response != null ? response.text() : "";

        } catch (SttException e) {
            throw e;
        } catch (Exception e) {
            log.error("[STTClient] Whisper API 호출 실패: {}", e.getMessage());
            throw new SttException("STT 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private record WhisperResponse(String text) {}
}
