package com.service.alaw.platform.contract.domain.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.contract.application.dto.ContractListResponse;
import com.service.alaw.platform.contract.application.dto.ContractResponse;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractQueryService {

    private final ContractRepository contractRepository;

    // 내 계약서 목록 조회
    public List<ContractListResponse> getMyContracts(Long userId) {
        List<Contract> contracts = contractRepository.findByUser_UserIdOrderByCreatedDateDesc(userId);

        return contracts.stream()
                .map(ContractListResponse::from)
                .collect(Collectors.toList());
    }

    // 계약서 상세 조회
    public ContractResponse getContract(Long contractId, Long userId) {
        Contract contract = contractRepository.findByContractIdAndUser_UserId(contractId, userId)
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));

        return ContractResponse.from(contract);
    }
}
