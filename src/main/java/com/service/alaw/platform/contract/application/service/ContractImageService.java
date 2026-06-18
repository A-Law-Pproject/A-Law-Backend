package com.service.alaw.platform.contract.application.service;

import com.service.alaw.common.exception.ContractNotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.contract.application.dto.image.ContractImageResponse;
import com.service.alaw.platform.contract.domain.document.OcrResultDocument;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.OcrResultDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractImageService {

    private final ContractValidator contractValidator;
    private final OcrResultDocumentRepository ocrResultDocumentRepository;

    @Transactional(readOnly = true)
    public ContractImageResponse getContractImage(Long contractId, Long userId) {
        Contract contract = contractValidator.validateContractOwnership(contractId, userId);

        if (contract.getAnalysisId() == null) {
            throw new ContractNotFoundException(CommonErrorCode.NOT_FOUND);
        }

        OcrResultDocument ocrResult = ocrResultDocumentRepository.findById(contract.getAnalysisId())
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        log.info("[ContractImage] 이미지 조회 - contractId={}", contractId);

        return new ContractImageResponse(
                contractId,
                ocrResult.getImageUrl(),
                ocrResult.getImageWidth(),
                ocrResult.getImageHeight()
        );
    }
}
