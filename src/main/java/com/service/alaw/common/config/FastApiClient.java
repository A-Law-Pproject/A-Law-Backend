//package com.service.alaw.common.config;
//
//import com.service.alaw.platform.contract.application.dto.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import reactor.core.publisher.Mono;
//import reactor.util.retry.Retry;
//
//import java.time.Duration;
//import java.util.Map;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class FastApiClient {
//
//    private final WebClient fastApiWebClient;
//
//    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//    // 방법 1: 동기 호출 (Blocking)
//    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//
//    /**
//     * 사기 탐지 분석 (동기)
//     * 사용 케이스: 즉시 결과가 필요한 경우
//     */
//    public FraudDetectionResponse analyzeFraudSync(String contractText) {
//        log.info("Calling FastAPI - Fraud Detection (Sync)");
//
//        return fastApiWebClient
//                .post()
//                .uri("/analyze/fraud-detection")
//                .bodyValue(Map.of(
//                        "text", contractText,
//                        "contract_id", generateContractId()
//                ))
//                .retrieve()
//                .bodyToMono(FraudDetectionResponse.class)
//                .timeout(Duration.ofSeconds(30))
//                .doOnError(error -> log.error("FastAPI call failed: {}", error.getMessage()))
//                .block(); // 동기 변환 (블로킹)
//    }
//
//    /**
//     * 용어 해설 (동기) - 빠른 응답
//     */
//    public TermExplanation explainTermSync(String term, String context, String surroundingText) {
//        log.info("Calling FastAPI - Term Explanation (Sync)");
//
//        return fastApiWebClient
//                .post()
//                .uri("/explain/term")
//                .bodyValue(Map.of(
//                        "term", term,
//                        "context", context,
//                        "surrounding_text", surroundingText
//                ))
//                .retrieve()
//                .bodyToMono(TermExplanation.class)
//                .timeout(Duration.ofSeconds(5)) // 짧은 타임아웃
//                .block();
//    }
//
//    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//    // 방법 2: 비동기 호출 (Non-blocking)
//    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//
//    /**
//     * 사기 탐지 분석 (비동기)
//     * 사용 케이스: 백그라운드 처리, 이벤트 기반
//     */
//    public Mono<FraudDetectionResponse> analyzeFraudAsync(String contractText) {
//        log.info("Calling FastAPI - Fraud Detection (Async)");
//
//        return fastApiWebClient
//                .post()
//                .uri("/analyze/fraud-detection")
//                .bodyValue(Map.of(
//                        "text", contractText,
//                        "contract_id", generateContractId()
//                ))
//                .retrieve()
//                .bodyToMono(FraudDetectionResponse.class)
//                .timeout(Duration.ofSeconds(30))
//                .doOnSuccess(response -> log.info("Analysis completed"))
//                .doOnError(error -> log.error("Analysis failed: {}", error.getMessage()));
//    }
//
//    /**
//     * 방법 3: 병렬 호출 (개선됨)
//     * - block()을 제거하여 완전한 Non-blocking 지원
//     * - 필요시 호출하는 쪽에서 block()을 하도록 선택권 부여
//     */
//    public Mono<CompleteAnalysisResponse> analyzeCompleteParallelAsync(String contractText) {
//        log.info("Calling FastAPI - Complete Analysis (Parallel Async)");
//
//        String sharedId = generateContractId(); // ID 통일
//
//        // 1. Mono 정의 (아직 실행 안 됨)
//        Mono<FraudDetectionResponse> fraudMono = fastApiWebClient.post()
//                .uri("/analyze/fraud-detection")
//                .bodyValue(Map.of("text", contractText, "contract_id", sharedId))
//                .retrieve()
//                .bodyToMono(FraudDetectionResponse.class)
//                .onErrorReturn(new FraudDetectionResponse(null, null)); // 에러 시 빈 객체 반환 등 예외처리 추천
//
//        Mono<MissingClausesResponse> missingMono = fastApiWebClient.post()
//                .uri("/analyze/missing-clauses")
//                .bodyValue(Map.of("text", contractText, "contract_id", sharedId))
//                .retrieve()
//                .bodyToMono(MissingClausesResponse.class);
//
//        Mono<IllegalClausesResponse> illegalMono = fastApiWebClient.post()
//                .uri("/analyze/illegal-clauses")
//                .bodyValue(Map.of("text", contractText, "contract_id", sharedId))
//                .retrieve()
//                .bodyToMono(IllegalClausesResponse.class);
//
//        // 2. 병렬 실행 및 합체
//        return Mono.zip(fraudMono, missingMono, illegalMono)
//                .map(tuple -> CompleteAnalysisResponse.builder()
//                        .fraudDetection(tuple.getT1())
//                        .missingClauses(tuple.getT2())
//                        .illegalClauses(tuple.getT3())
//                        .build())
//                .timeout(Duration.ofSeconds(45));
//    }
//
//    /**
//     * 방법 4: 안전한 재시도 로직 (수정됨)
//     */
//    public FraudDetectionResponse analyzeFraudWithRetry(String contractText) {
//        log.info("Calling FastAPI with Retry");
//
//        return fastApiWebClient
//                .post()
//                .uri("/analyze/fraud-detection")
//                .bodyValue(Map.of("text", contractText, "contract_id", generateContractId()))
//                .retrieve()
//                .bodyToMono(FraudDetectionResponse.class)
//                // ★ 핵심 수정: 5xx 에러만 1초 간격으로 3회 재시도
//                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
//                        .filter(throwable -> isServerError(throwable)))
//                .timeout(Duration.ofSeconds(30)) // 전체 시도 시간 제한
//                .block();
//    }
//
//    private boolean isServerError(Throwable throwable) {
//        if (throwable instanceof WebClientResponseException e) {
//            // 500번대 에러만 재시도 (400 Bad Request는 재시도 X)
//            return e.getStatusCode().is5xxServerError();
//        }
//        // 연결 타임아웃 등 네트워크 에러는 재시도
//        return true;
//    }
//
//    private String generateContractId() {
//        return UUID.randomUUID().toString();
//    }
//}
