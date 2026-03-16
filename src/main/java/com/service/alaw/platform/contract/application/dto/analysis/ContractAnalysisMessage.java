package com.service.alaw.platform.contract.application.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record ContractAnalysisMessage(
        @JsonProperty("jobId")
        String jobId,

        @JsonProperty("contractId")
        Long contractId,

        @JsonProperty("s3Key")
        String s3Key,

        @JsonProperty("userId")
        Long userId
) implements Serializable {

    private static final long serialVersionUID = 1L;
}
