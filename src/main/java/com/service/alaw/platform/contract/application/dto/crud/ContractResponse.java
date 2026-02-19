package com.service.alaw.platform.contract.application.dto.crud;

import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.entity.ContractStatus;
import com.service.alaw.platform.contract.domain.entity.ContractType;
import java.time.LocalDateTime;

public record ContractResponse(
    Long contractId,
    String analysisId,
    String title,
    String fileUrl,
    boolean bookmark,
    ContractType contractType,
    ContractStatus status,
    String rawText,
    LocalDateTime createdAt) {
  public static ContractResponse from(Contract contract) {
    return new ContractResponse(
        contract.getContractId(),
        contract.getAnalysisId(),
        contract.getTitle(),
        contract.getFileUrl(),
        contract.isBookmark(),
        contract.getContractType(),
        contract.getStatus(),
        contract.getRawText(),
        contract.getCreatedDate());
  }
}
