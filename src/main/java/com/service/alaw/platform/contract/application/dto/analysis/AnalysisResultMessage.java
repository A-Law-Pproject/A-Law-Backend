package com.service.alaw.platform.contract.application.dto.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * FastAPI → Spring 분석 결과 메시지
 * FastAPI ContractAnalysisResult.to_rabbitmq_message() 의 camelCase 포맷과 매핑
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalysisResultMessage(
        @JsonProperty("jobId")          String jobId,
        @JsonProperty("contractId")     Long contractId,
        @JsonProperty("status")         String status,           // COMPLETED | FAILED
        @JsonProperty("summary")        SummaryDto summary,
        @JsonProperty("riskAnalysis")   RiskAnalysisDto riskAnalysis,
        @JsonProperty("processingTimeMs") int processingTimeMs,
        @JsonProperty("completedAt")    String completedAt,
        @JsonProperty("errorMessage")   String errorMessage
) implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean isSuccess() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SummaryDto(
            String title,
            String summaryText,
            List<String> keyTerms
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RiskAnalysisDto(
            int totalClauses,
            int riskCount,
            int cautionCount,
            int safetyCount,
            double riskPercentage,
            List<ClauseDto> clauseResults
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ClauseDto(
            String clauseTitle,
            String clauseContent,
            String riskLevel,
            String recommendation,
            String legalReference
    ) {}
}
