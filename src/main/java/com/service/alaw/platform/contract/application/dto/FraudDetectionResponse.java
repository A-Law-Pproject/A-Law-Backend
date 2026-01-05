package com.service.alaw.platform.contract.application.dto;

import java.util.List;

public record FraudDetectionResponse(
        List<FraudRisk> fraudRisks,
//        List<MissingClause> missingClauses,
//        List<IllegalClause> illegalClauses,
        Double riskScore
) {

}
