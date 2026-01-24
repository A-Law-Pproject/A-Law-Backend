package com.service.alaw.platform.contract.application.dto;

import lombok.Builder;

@Builder
public record FraudRisk(
        String clause,
        String riskType,
        String explanation,
        String severity
) {}
