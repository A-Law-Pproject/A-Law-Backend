package com.service.alaw.contractmanagement.dto;

import java.time.Instant;

public record ContractResponse(
    Long id,
    String title,
    String fileUrl,
    String content,
    boolean bookmarked,
    Instant createdAt,
    Instant updatedAt
) {}
