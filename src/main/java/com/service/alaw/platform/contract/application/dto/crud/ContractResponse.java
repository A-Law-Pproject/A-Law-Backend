package com.service.alaw.platform.contract.application.dto.crud;

import com.service.alaw.platform.contract.application.dto.ocr.OcrBlock;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.entity.ContractStatus;
import com.service.alaw.platform.contract.domain.entity.ContractType;
import java.time.LocalDateTime;
import java.util.List;

public record ContractResponse(
    Long contractId,
    String analysisId,
    String title,
    String fileUrl,
    boolean bookmark,
    ContractType contractType,
    ContractStatus status,
    String rawText,
    String markdown,
    List<OcrBlock> words,
    LocalDateTime createdAt) {
  public static ContractResponse from(Contract contract) {
    return from(contract, contract.getAnalysisId(), contract.getRawText(), null, null);
  }

  public static ContractResponse from(
      Contract contract, String analysisId, String rawText, String markdown, List<OcrBlock> words) {
    return new ContractResponse(
        contract.getContractId(),
        analysisId,
        contract.getTitle(),
        contract.getFileUrl(),
        contract.isBookmark(),
        contract.getContractType(),
        contract.getStatus(),
        rawText,
        markdown,
        words,
        contract.getCreatedDate());
  }
}
