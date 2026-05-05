package com.service.alaw.platform.voice.presentation.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Voice Record", description = "음성 녹음 API")
public interface VoiceSseSpec {

    @Operation(
            summary = "팩트체크 분석 결과 SSE 구독",
            description = """
                    음성 팩트체크 분석 결과를 Server-Sent Events(SSE)로 실시간 수신합니다.
                    /analyze 요청 응답으로 받은 jobId를 사용하여 구독하세요.

                    **이벤트 순서:**
                    1. `connection` - 연결 확인
                    2. `voice_fact_check_result` - 팩트체크 결과 (voiceRecordId, transcript, factCheckItems)
                    3. `voice_fact_check_complete` - 완료 신호 (status, jobId, voiceRecordId)

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
            @Parameter(description = "/analyze 요청 시 발급된 jobId", required = true)
            String jobId);
}
