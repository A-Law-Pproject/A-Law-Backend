package com.service.alaw.platform.contract.application.dto;

import java.time.LocalDateTime;

public record AnalysisStatus(
        String jobId,
        String status,
        LocalDateTime submittedAt
) {}
