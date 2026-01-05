package com.service.alaw.platform.contract.application.dto;

import lombok.Builder;

@Builder
public record CompleteAnalysisResponse(
         FraudDetectionResponse fraudDetection,
         MissingClausesResponse missingClauses,
         IllegalClausesResponse illegalClauses
) {
}
