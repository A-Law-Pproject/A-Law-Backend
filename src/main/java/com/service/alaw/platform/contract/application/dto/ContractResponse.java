package com.service.alaw.platform.contract.application.dto;

import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.entity.ContractStatus;
import com.service.alaw.platform.contract.domain.entity.ContractType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContractResponse {

    private Long contractId;
    private String analysisId;
    private String title;
    private String fileUrl;
    private boolean bookmark;
    private ContractType contractType;
    private ContractStatus status;
    private String rawText;
    private LocalDateTime createdAt;

    public static ContractResponse from(Contract contract) {
        return ContractResponse.builder()
                .contractId(contract.getContractId())
                .analysisId(contract.getAnalysisId())
                .title(contract.getTitle())
                .fileUrl(contract.getFileUrl())
                .bookmark(contract.isBookmark())
                .contractType(contract.getContractType())
                .status(contract.getStatus())
                .rawText(contract.getRawText())
                .createdAt(contract.getCreatedDate())
                .build();
    }
}
