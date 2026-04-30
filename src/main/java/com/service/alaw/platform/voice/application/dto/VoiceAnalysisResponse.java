package com.service.alaw.platform.voice.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * FastAPI POST /ai/voice/analyze-s3 응답 DTO.
 * FastAPI VoiceAnalysisResponse 스키마와 camelCase/snake_case 필드 이름을 일치시킨다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VoiceAnalysisResponse(

        @JsonProperty("success")
        boolean success,

        @JsonProperty("transcript")
        String transcript,

        @JsonProperty("summary")
        VoiceAnalysisSummary summary,

        @JsonProperty("segments")
        List<SegmentResult> segments,

        @JsonProperty("agreements")
        List<AgreementItem> agreements,

        @JsonProperty("audio_meta")
        VoiceAudioMeta audioMeta,

        @JsonProperty("processing_time_ms")
        Integer processingTimeMs,

        @JsonProperty("error_message")
        String errorMessage

) {

    /** 분석 요약 (summary, key_points, risk_items) */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VoiceAnalysisSummary(

            @JsonProperty("summary")
            String summary,

            @JsonProperty("key_points")
            List<String> keyPoints,

            @JsonProperty("risk_items")
            List<VoiceRiskItem> riskItems
    ) {}

    /** 개별 위험 항목 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VoiceRiskItem(

            @JsonProperty("risk_type")
            String riskType,

            @JsonProperty("severity")
            String severity,

            @JsonProperty("detail")
            String detail,

            @JsonProperty("timestamp_str")
            String timestampStr
    ) {}

    /** STT 발화 세그먼트 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SegmentResult(

            @JsonProperty("id")
            String id,

            @JsonProperty("start_time")
            Double startTime,

            @JsonProperty("end_time")
            Double endTime,

            @JsonProperty("text")
            String text,

            @JsonProperty("speaker")
            String speaker,

            @JsonProperty("timestamp_str")
            String timestampStr
    ) {}

    /** 합의 항목 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AgreementItem(

            @JsonProperty("segment_id")
            String segmentId,

            @JsonProperty("agreement_type")
            String agreementType,

            @JsonProperty("value")
            String value,

            @JsonProperty("context")
            String context,

            @JsonProperty("timestamp_str")
            String timestampStr
    ) {}

    /** 음성 파일 메타데이터 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VoiceAudioMeta(

            @JsonProperty("file_hash")
            String fileHash,

            @JsonProperty("original_filename")
            String originalFilename,

            @JsonProperty("created_at")
            String createdAt,

            @JsonProperty("s3_key")
            String s3Key,

            @JsonProperty("source_id")
            String sourceId,

            @JsonProperty("file_size_bytes")
            Long fileSizeBytes,

            @JsonProperty("content_type")
            String contentType
    ) {}
}
