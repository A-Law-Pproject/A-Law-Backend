package com.service.alaw.contractmanagement.dto;

import java.time.Instant;

public record ContractListItemResponse(
    Long id,
    String title,
    boolean bookmarked,
    Instant createdAt,
    Instant updatedAt
) {}
