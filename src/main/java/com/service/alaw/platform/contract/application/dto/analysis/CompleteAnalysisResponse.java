package com.service.alaw.platform.contract.application.dto.analysis;

import com.service.alaw.platform.contract.application.dto.risk.FraudDetectionResponse;
import com.service.alaw.platform.contract.application.dto.risk.IllegalClausesResponse;
import com.service.alaw.platform.contract.application.dto.risk.MissingClausesResponse;

public record CompleteAnalysisResponse(
         FraudDetectionResponse fraudDetection,
         MissingClausesResponse missingClauses,
         IllegalClausesResponse illegalClauses
) {
}
