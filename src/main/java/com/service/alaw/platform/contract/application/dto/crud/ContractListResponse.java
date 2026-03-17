package com.service.alaw.platform.contract.application.dto.crud;

import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.entity.ContractStatus;
import com.service.alaw.platform.contract.domain.entity.ContractType;
import java.time.LocalDateTime;

public record ContractListResponse(
    Long contractId,
    String title,
    boolean bookmark,
    ContractType contractType,
    ContractStatus status,
    LocalDateTime createdAt) {
  public static ContractListResponse from(Contract contract) {
    return new ContractListResponse(
        contract.getContractId(),
        contract.getTitle(),
        contract.isBookmark(),
        contract.getContractType(),
        contract.getStatus(),
        contract.getCreatedDate());
  }
}
