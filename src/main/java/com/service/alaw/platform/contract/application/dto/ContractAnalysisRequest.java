package com.service.alaw.platform.contract.application.dto;

import lombok.Builder;

@Builder
public record ContractAnalysisRequest(
        String contractId,
        String userId,
        String text,
        boolean urgent,
        boolean premiumUser
) {}
