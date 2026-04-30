package com.service.alaw.infra.ai;

import com.service.alaw.common.exception.FastApiException;
import com.service.alaw.platform.voice.application.dto.VoiceAnalysisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * FastAPI POST /ai/voice/analyze-s3 를 동기 호출하는 WebClient 클라이언트.
 * voice-only(계약서 없음) 분석 요청에 사용된다.
 */
@Slf4j
@Component
public class VoiceAnalysisClient {

    private final WebClient webClient;
    private final int readTimeout;

    public VoiceAnalysisClient(
            WebClient fastApiWebClient,
            @Value("${fastapi.timeout.read}") int readTimeout) {
        this.webClient = fastApiWebClient;
        this.readTimeout = readTimeout;
    }

    /**
     * S3 에 업로드된 음성 파일을 FastAPI 로 분석 요청한다.
     *
     * @param s3Key    S3 오브젝트 키
     * @param sourceId 메타데이터 분류용 식별자 (voice_record ID 등)
     * @return VoiceAnalysisResponse FastAPI 응답
     */
    public VoiceAnalysisResponse analyzeVoiceOnly(String s3Key, String sourceId) {
        log.info("[VoiceAnalysisClient] voice-only 분석 요청 - s3Key={}, sourceId={}", s3Key, sourceId);

        try {
            VoiceAnalysisResponse response = webClient
                    .post()
                    .uri("/ai/voice/analyze-s3")
                    .bodyValue(Map.of("s3_key", s3Key, "source_id", sourceId))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            resp -> resp.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("[VoiceAnalysisClient] FastAPI 오류 응답: {}", body);
                                        return Mono.error(new FastApiException("voice-only 분석 실패: " + body));
                                    })
                    )
                    .bodyToMono(VoiceAnalysisResponse.class)
                    .block(Duration.ofMillis(readTimeout));

            log.info("[VoiceAnalysisClient] voice-only 분석 완료 - success={}", response != null && response.success());
            return response;

        } catch (FastApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[VoiceAnalysisClient] FastAPI 호출 실패 - s3Key={}, error={}", s3Key, e.getMessage());
            throw new FastApiException("voice-only 분석 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
