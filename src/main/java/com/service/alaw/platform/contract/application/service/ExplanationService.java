package com.service.alaw.platform.contract.application.service;

import com.service.alaw.infra.ai.AIClient;
import com.service.alaw.platform.contract.application.dto.explanation.EasyExplanationRequest;
import com.service.alaw.platform.contract.application.dto.explanation.EasyExplanationResponse;
import com.service.alaw.platform.contract.domain.document.ExplanationDocument;
import com.service.alaw.platform.contract.domain.repository.ExplanationDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExplanationService {

  private final AIClient aiClient;
  private final ExplanationDocumentRepository explanationDocumentRepository;

  public EasyExplanationResponse getEasyExplanation(EasyExplanationRequest request) {
    log.info("쉬운 말 요약 요청 - contractId: {}", request.contractId());

    EasyExplanationResponse response = aiClient.getEasyExplanation(request.term(), request.originalSentence());

    if (request.contractId() != null) {
      saveExplanation(request.contractId(), request.originalSentence(), response.easyExplanation());
    }

    return response;
  }

  private void saveExplanation(Long contractId, String originalText, String explanation) {
    ExplanationDocument document = ExplanationDocument.of(contractId, originalText, explanation);
    explanationDocumentRepository.save(document);
    log.info("쉬운 말 요약 저장 완료 - contractId: {}", contractId);
  }
}
