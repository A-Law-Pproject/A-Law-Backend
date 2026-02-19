package com.service.alaw.platform.contract.application.dto.crud;

import com.service.alaw.platform.contract.domain.entity.ContractType;
import jakarta.validation.constraints.NotBlank;

public record ContractCreateRequest(
    @NotBlank(message = "계약서 제목은 필수입니다") String title, String fileUrl, ContractType contractType) {}
