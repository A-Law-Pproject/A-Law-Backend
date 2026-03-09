package com.service.alaw.platform.contract.presentation;

import com.service.alaw.platform.contract.application.dto.analysis.AnalysisResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * RabbitMQ 동작 확인용 테스트 컨트롤러 (local 프로파일 전용)
 * OCR 없이 mock 분석 결과를 ai.result.queue로 직접 publish
 */
@Slf4j
@Profile("local")
@RestController
@RequestMapping("/test/rabbitmq")
@RequiredArgsConstructor
public class RabbitMQTestController {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 성공 케이스: mock 분석 결과를 queue에 publish
     * SSE 구독 중인 jobId로 결과가 전달되는지 확인
     *
     * @param jobId SSE 구독 시 사용한 jobId (없으면 자동 생성)
     */
    @PostMapping("/publish")
    public ResponseEntity<String> publishMockResult(
            @RequestParam(defaultValue = "") String jobId,
            @RequestParam(defaultValue = "1") Long contractId
    ) {
        String resolvedJobId = jobId.isBlank() ? UUID.randomUUID().toString() : jobId;

        AnalysisResultMessage message = new AnalysisResultMessage(
                resolvedJobId,
                contractId,
                "COMPLETED",
                new AnalysisResultMessage.SummaryDto(
                        "테스트 계약서",
                        "이것은 RabbitMQ 동작 확인을 위한 mock 분석 결과입니다.",
                        List.of("임대", "보증금", "계약기간")
                ),
                new AnalysisResultMessage.RiskAnalysisDto(
                        3, 1, 1, 1, 33.3,
                        List.of(
                                new AnalysisResultMessage.ClauseDto(
                                        "보증금 반환 조항",
                                        "계약 만료 시 보증금을 반환한다.",
                                        "RISK",
                                        "반환 기한 명시 필요",
                                        "주택임대차보호법 제3조"
                                ),
                                new AnalysisResultMessage.ClauseDto(
                                        "계약 갱신 조항",
                                        "임차인은 계약 갱신을 요구할 수 있다.",
                                        "CAUTION",
                                        "갱신 거절 사유 확인 필요",
                                        "주택임대차보호법 제6조의3"
                                ),
                                new AnalysisResultMessage.ClauseDto(
                                        "관리비 조항",
                                        "관리비는 월 10만원으로 한다.",
                                        "SAFE",
                                        "적정 수준",
                                        null
                                )
                        )
                ),
                500,
                LocalDateTime.now().toString(),
                null
        );

        rabbitTemplate.convertAndSend("contract.analysis.result", "ai.result", message);
        log.info("[RabbitMQ Test] Mock 결과 publish 완료 - jobId={}, contractId={}", resolvedJobId, contractId);

        return ResponseEntity.ok("Published! jobId=" + resolvedJobId);
    }

    /**
     * 실패 케이스: 오류 상황 mock publish
     */
    @PostMapping("/publish/fail")
    public ResponseEntity<String> publishMockFailure(
            @RequestParam(defaultValue = "") String jobId,
            @RequestParam(defaultValue = "1") Long contractId
    ) {
        String resolvedJobId = jobId.isBlank() ? UUID.randomUUID().toString() : jobId;

        AnalysisResultMessage message = new AnalysisResultMessage(
                resolvedJobId,
                contractId,
                "FAILED",
                null,
                null,
                0,
                LocalDateTime.now().toString(),
                "테스트용 오류 메시지"
        );

        rabbitTemplate.convertAndSend("contract.analysis.result", "ai.result", message);
        log.info("[RabbitMQ Test] Mock 실패 결과 publish 완료 - jobId={}", resolvedJobId);

        return ResponseEntity.ok("Published failure! jobId=" + resolvedJobId);
    }
}
