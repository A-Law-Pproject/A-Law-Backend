package com.service.alaw.platform.contract.application.dto.crud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContractUpdateRequest(
    @NotBlank(message = "계약서 이름은 필수입니다.")
    @Size(max = 255, message = "계약서 이름은 255자를 초과할 수 없습니다.")
    String title) {}
