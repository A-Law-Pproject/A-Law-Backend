package com.service.alaw.platform.voice.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * FastAPI → Spring 음성 팩트체크 결과 메시지
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VoiceFactCheckResultMessage(
        @JsonProperty("voiceRecordId")  Long voiceRecordId,
        @JsonProperty("contractId")     Long contractId,
        @JsonProperty("jobId")          String jobId,
        @JsonProperty("status")         String status,        // COMPLETED | FAILED
        @JsonProperty("transcript")     String transcript,
        @JsonProperty("factCheckItems") List<FactCheckItemDto> factCheckItems,
        @JsonProperty("processingTimeMs") Integer processingTimeMs,
        @JsonProperty("errorMessage")   String errorMessage
) implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean isSuccess() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FactCheckItemDto(
            @JsonProperty("claim")           String claim,
            @JsonProperty("contractContent") String contractContent,
            @JsonProperty("isMatch")         boolean isMatch,
            @JsonProperty("severity")        String severity   // HIGH | MEDIUM | LOW | null
    ) {}
}
