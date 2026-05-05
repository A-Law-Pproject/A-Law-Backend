package com.service.alaw.platform.contract.presentation.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Contract Analysis", description = "계약서 OCR 및 분석 API")
public interface ContractSseSpec {

    @Operation(
            summary = "[완료] Contract Analysis 결과 SSE 구독",
            description = """
                    Contract Analysis 결과를 Server-Sent Events(SSE)로 실시간 수신합니다.
                    OCR 요청 응답으로 받은 jobId를 사용하여 구독하세요.

                    **이벤트 순서:**
                    1. `connection` - 연결 확인
                    2. `summary_result` - 요약 정보 (title, summaryText, keyTerms)
                    3. `analysis_result` - 리스크 분석 (totalClauses, riskCount, cautionCount, safetyCount, clauseResults)
                    4. `analysis_complete` - 완료 신호 (status, jobId, processingTimeMs)

                    실패 시: `error` 이벤트 수신
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "SSE 스트림 연결 성공",
                    content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE)),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자")
    })
    SseEmitter subscribe(
            @Parameter(description = "OCR 요청 시 발급된 jobId", required = true)
            String jobId);
}
