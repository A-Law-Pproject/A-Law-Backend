package com.service.alaw.platform.contract.application.dto.crud;

import com.service.alaw.platform.contract.domain.entity.ContractType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContractCreateRequest(
    @NotNull(message = "계약서 ID는 필수입니다.")
    Long contractId,
    @NotBlank(message = "계약서 이름은 필수입니다.")
    @Size(max = 255, message = "계약서 이름은 255자를 초과할 수 없습니다.")
    String title,
    ContractType contractType) {}
