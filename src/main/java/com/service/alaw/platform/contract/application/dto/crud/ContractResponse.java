package com.service.alaw.platform.contract.application.dto.crud;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisDetailResponse;
import com.service.alaw.platform.contract.application.dto.ocr.OcrBlock;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.document.OcrResultDocument;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.entity.ContractStatus;
import com.service.alaw.platform.contract.domain.entity.ContractType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public record ContractResponse(
    Long contractId,
    String analysisId,
    String title,
    String fileUrl,
    @JsonProperty("image_url")
    String imageUrl,
    boolean bookmark,
    ContractType contractType,
    ContractStatus status,
    String rawText,
    @JsonProperty("full_text")
    String fullText,
    String markdown,
    @JsonProperty("image_width")
    Integer imageWidth,
    @JsonProperty("image_height")
    Integer imageHeight,
    @JsonProperty("contract_data")
    Map<String, Object> contractData,
    Map<String, Object> validation,
    List<OcrBlock> words,
    List<String> warnings,
    ContractAnalysisDetailResponse.SummaryResult summary,
    ContractAnalysisDetailResponse.RiskAnalysisResult riskAnalysis,
    LocalDateTime createdAt) {

  public static ContractResponse from(Contract contract) {
    return from(contract, contract.getAnalysisId(), contract.getRawText(), null, null);
  }

  public static ContractResponse from(
      Contract contract,
      String analysisId,
      String rawText,
      OcrResultDocument ocrDocument,
      ContractAnalysisDocument analysisDocument) {
    String resolvedImageUrl =
        ocrDocument != null && StringUtils.hasText(ocrDocument.getImageUrl())
            ? ocrDocument.getImageUrl()
            : contract.getFileUrl();
    String resolvedFullText =
        StringUtils.hasText(rawText)
            ? rawText
            : ocrDocument != null ? ocrDocument.getFullText() : null;

    return new ContractResponse(
        contract.getContractId(),
        analysisId,
        contract.getTitle(),
        contract.getFileUrl(),
        resolvedImageUrl,
        contract.isBookmark(),
        contract.getContractType(),
        contract.getStatus(),
        resolvedFullText,
        resolvedFullText,
        ocrDocument != null ? ocrDocument.getMarkdown() : null,
        ocrDocument != null ? ocrDocument.getImageWidth() : null,
        ocrDocument != null ? ocrDocument.getImageHeight() : null,
        ocrDocument != null ? ocrDocument.getContractData() : null,
        ocrDocument != null ? ocrDocument.getValidation() : null,
        ocrDocument != null ? ocrDocument.getWords() : null,
        ocrDocument != null ? ocrDocument.getWarnings() : null,
        analysisDocument != null
            ? ContractAnalysisDetailResponse.SummaryResult.from(analysisDocument)
            : null,
        analysisDocument != null
            ? ContractAnalysisDetailResponse.RiskAnalysisResult.from(analysisDocument)
            : null,
        contract.getCreatedDate());
  }
}
