package com.service.alaw.platform.contract.application.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record AnalysisResultMessage(
        @JsonProperty("s3_key")
        String s3Key,

        @JsonProperty("status")
        String status,

        @JsonProperty("summary")
        String summary,

        @JsonProperty("risk_score")
        Integer riskScore,

        @JsonProperty("risk_details")
        String riskDetails
) implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean isSuccess() {
        return "COMPLETED".equalsIgnoreCase(status);
    }
}
