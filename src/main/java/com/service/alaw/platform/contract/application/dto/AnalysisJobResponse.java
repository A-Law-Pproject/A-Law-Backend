package com.service.alaw.platform.contract.application.dto;

public record AnalysisJobResponse(
        String jobId,
        String status,
        int estimatedCompletionTime,
        String websocketUrl
) {}
