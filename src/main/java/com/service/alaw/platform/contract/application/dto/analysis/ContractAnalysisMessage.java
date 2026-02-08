package com.service.alaw.platform.contract.application.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record ContractAnalysisMessage(
        @JsonProperty("job_id")
        String jobId,

        @JsonProperty("contract_id")
        Long contractId,

        @JsonProperty("s3_key")
        String s3Key,

        @JsonProperty("user_id")
        Long userId
) implements Serializable {

    private static final long serialVersionUID = 1L;
}
