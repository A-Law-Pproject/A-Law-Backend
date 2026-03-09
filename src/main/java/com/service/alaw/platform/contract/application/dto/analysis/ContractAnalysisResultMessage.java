package com.service.alaw.platform.contract.application.dto.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * FastAPI → Spring 분석 결과 메시지 (Job 상태 추적용)
 * ContractAnalysisConsumer(SSE/MongoDB)와 분리된 Job 트래킹 전용 consumer에서 사용
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContractAnalysisResultMessage(
        @JsonProperty("jobId")           String jobId,
        @JsonProperty("contractId")      Long contractId,
        @JsonProperty("status")          String status,
        @JsonProperty("summary")         AnalysisResultMessage.SummaryDto summary,
        @JsonProperty("riskAnalysis")    AnalysisResultMessage.RiskAnalysisDto riskAnalysis,
        @JsonProperty("processingTimeMs") long processingTimeMs,
        @JsonProperty("errorMessage")    String errorMessage
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
