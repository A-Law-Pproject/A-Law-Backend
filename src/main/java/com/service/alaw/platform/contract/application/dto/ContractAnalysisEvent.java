package com.service.alaw.platform.contract.application.dto;

public record ContractAnalysisEvent(
        String jobId,
        String contractId,
        String userId,
        String text,
        int priority,
        long timestamp
) {}
