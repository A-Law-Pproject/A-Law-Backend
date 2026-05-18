package com.service.alaw.platform.contract.application.dto.crud;

import com.service.alaw.platform.contract.domain.entity.ContractType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContractCreateRequest(
    @NotNull(message = "계약서 ID는 필수입니다") Long contractId,
    @NotBlank(message = "계약서 제목은 필수입니다") String title,
    ContractType contractType) {}
