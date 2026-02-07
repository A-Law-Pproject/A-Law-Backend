package com.service.alaw.platform.contract.application.dto;

import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.entity.ContractStatus;
import com.service.alaw.platform.contract.domain.entity.ContractType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContractListResponse {

    private Long contractId;
    private String title;
    private boolean bookmark;
    private ContractType contractType;
    private ContractStatus status;
    private LocalDateTime createdAt;

    public static ContractListResponse from(Contract contract) {
        return ContractListResponse.builder()
                .contractId(contract.getContractId())
                .title(contract.getTitle())
                .bookmark(contract.isBookmark())
                .contractType(contract.getContractType())
                .status(contract.getStatus())
                .createdAt(contract.getCreatedDate())
                .build();
    }
}
