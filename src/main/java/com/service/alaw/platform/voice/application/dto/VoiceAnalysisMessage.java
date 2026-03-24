package com.service.alaw.platform.voice.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Spring → FastAPI 음성 분석 요청 메시지
 */
public record VoiceAnalysisMessage(
        @JsonProperty("voiceRecordId") Long voiceRecordId,
        @JsonProperty("contractId")   Long contractId,
        @JsonProperty("userId")       Long userId,
        @JsonProperty("jobId")        String jobId,
        @JsonProperty("s3Key")        String s3Key,
        @JsonProperty("rawText")      String rawText
) implements Serializable {

    private static final long serialVersionUID = 1L;
}
