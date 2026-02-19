package com.service.alaw.platform.contract.application.service;

import com.service.alaw.infra.ai.TextToImageClient;
import com.service.alaw.platform.contract.application.dto.image.TextToImageRequest;
import com.service.alaw.platform.contract.application.dto.image.TextToImageResponse;
import com.service.alaw.platform.contract.domain.entity.Contract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextToImageService {

  private final TextToImageClient textToImageClient;
  private final ContractValidator contractValidator;

  @Transactional
  public TextToImageResponse convertTextToImage(
      Long contractId, Long userId, TextToImageRequest request) {
    log.info("텍스트→이미지 변환 요청 - contractId: {}, userId: {}", contractId, userId);

    // 1. 계약서 소유권 검증
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);

    // 2. FastAPI로 텍스트→이미지 변환 요청
    TextToImageResponse response =
        textToImageClient.convertTextToImage(contractId, request.textContent());

    // 3. 계약서에 이미지 URL 업데이트 (필요시)
    if (response.imageUrl() != null) {
      contract.updateFileUrl(response.imageUrl());
      log.info("계약서 이미지 URL 업데이트 완료 - contractId: {}", contractId);
    }

    return response;
  }
}
