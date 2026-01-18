package com.service.alaw.contractmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContractUpdateRequest(
    @NotBlank
    @Size(max = 200)
    String title
) {}
