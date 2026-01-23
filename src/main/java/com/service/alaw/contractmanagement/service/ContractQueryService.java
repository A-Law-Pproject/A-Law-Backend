package com.service.alaw.contractmanagement.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.contractmanagement.dto.ContractListResponse;
import com.service.alaw.contractmanagement.dto.ContractResponse;
import com.service.alaw.contractmanagement.entity.Contract;
import com.service.alaw.contractmanagement.repository.ContractRepository;
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
    public List<ContractListResponse> getMyContracts(Long memberId) {
        List<Contract> contracts = contractRepository.findByMemberIdOrderByCreatedDateDesc(memberId);

        return contracts.stream()
                .map(ContractListResponse::from)
                .collect(Collectors.toList());
    }

    // 계약서 상세 조회
    public ContractResponse getContract(Long contractId, Long memberId) {
        Contract contract = contractRepository.findByContractIdAndMemberId(contractId, memberId)
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));

        return ContractResponse.from(contract);
    }
}
