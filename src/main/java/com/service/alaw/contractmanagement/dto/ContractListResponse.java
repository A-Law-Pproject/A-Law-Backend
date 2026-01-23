package com.service.alaw.contractmanagement.dto;

import com.service.alaw.contractmanagement.entity.Contract;
import com.service.alaw.contractmanagement.entity.ContractStatus;
import com.service.alaw.contractmanagement.entity.ContractType;
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
