package com.service.alaw.platform.voice.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Spring → FastAPI 음성 분석 요청 메시지.
 * 계약서 원문(rawText)은 FastAPI 가 MongoDB OCR 결과에서 직접 조회하므로 전달하지 않는다.
 */
public record VoiceAnalysisMessage(
        @JsonProperty("voiceRecordId") Long voiceRecordId,
        @JsonProperty("contractId")    Long contractId,
        @JsonProperty("userId")        Long userId,
        @JsonProperty("jobId")         String jobId,
        @JsonProperty("s3Key")         String s3Key
) implements Serializable {

    private static final long serialVersionUID = 1L;
}
