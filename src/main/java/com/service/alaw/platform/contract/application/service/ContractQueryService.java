package com.service.alaw.platform.contract.application.service;

import com.service.alaw.platform.contract.application.dto.crud.ContractListResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.document.OcrResultDocument;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractAnalysisDocumentRepository;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import com.service.alaw.platform.contract.domain.repository.OcrResultDocumentRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractQueryService {

  private final ContractRepository contractRepository;
  private final ContractValidator contractValidator;
  private final OcrResultDocumentRepository ocrResultDocumentRepository;
  private final ContractAnalysisDocumentRepository contractAnalysisDocumentRepository;

  @Cacheable(cacheNames = "contracts-list", key = "#userId")
  public List<ContractListResponse> getMyContracts(Long userId) {
    log.info("계약서 목록 조회 시작 - userId: {}", userId);
    List<Contract> contracts = contractRepository.findByUser_UserIdAndUserSavedTrueOrderByCreatedDateDesc(userId);
    log.info("계약서 목록 조회 완료 - userId: {}, 건수: {}", userId, contracts.size());
    return convertToListResponses(contracts);
  }

  @Cacheable(cacheNames = "contracts-detail", key = "#contractId")
  public ContractResponse getContract(Long contractId, Long userId) {
    log.info("계약서 단건 조회 시작 - contractId: {}, userId: {}", contractId, userId);
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);
    Optional<ContractAnalysisDocument> analysisDocument =
        contractAnalysisDocumentRepository.findByContractId(contract.getContractId());
    log.info("계약서 단건 조회 완료 - contractId: {}", contractId);
    return ContractResponse.from(
        contract,
        resolveAnalysisId(contract, analysisDocument),
        resolveRawText(contract, analysisDocument));
  }

  private List<ContractListResponse> convertToListResponses(List<Contract> contracts) {
    return contracts.stream().map(ContractListResponse::from).toList();
  }

  private String resolveAnalysisId(
      Contract contract, Optional<ContractAnalysisDocument> analysisDocument) {
    if (StringUtils.hasText(contract.getAnalysisId())) {
      return contract.getAnalysisId();
    }

    return analysisDocument
        .map(ContractAnalysisDocument::getJobId)
        .orElse(null);
  }

  private String resolveRawText(
      Contract contract, Optional<ContractAnalysisDocument> analysisDocument) {
    if (StringUtils.hasText(contract.getRawText())) {
      return contract.getRawText();
    }

    // fileUrl alone is user-controlled for manually created contracts, so only
    // allow fallback when this contract has a trusted analysis document.
    if (analysisDocument.isEmpty()) {
      return null;
    }

    if (!StringUtils.hasText(contract.getFileUrl())) {
      return null;
    }

    return ocrResultDocumentRepository
        .findByImageUrl(contract.getFileUrl())
        .map(OcrResultDocument::getFullText)
        .orElse(null);
  }
}
