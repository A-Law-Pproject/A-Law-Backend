package com.service.alaw.platform.voice.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * POST /api/v1/voice-records/{voiceRecordId}/analyze 응답.
 * - async=true  : RabbitMQ 비동기 팩트체크 → jobId 로 SSE 구독
 * - async=false : FastAPI 동기 분석 → result 에 즉시 결과 포함
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VoiceAnalyzeStartResponse(
        String jobId,
        boolean async,
        VoiceAnalysisResponse result
) {}
