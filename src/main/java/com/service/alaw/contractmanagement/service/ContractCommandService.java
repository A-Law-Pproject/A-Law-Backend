package com.service.alaw.contractmanagement.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.contractmanagement.dto.ContractCreateRequest;
import com.service.alaw.contractmanagement.dto.ContractResponse;
import com.service.alaw.contractmanagement.dto.ContractUpdateRequest;
import com.service.alaw.contractmanagement.entity.Contract;
import com.service.alaw.contractmanagement.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractCommandService {

    private final ContractRepository contractRepository;

    // 내 문서에 저장하기 (계약서 생성)
    public ContractResponse createContract(Long memberId, ContractCreateRequest request) {
        Contract contract = Contract.builder()
                .memberId(memberId)
                .title(request.getTitle())
                .fileUrl(request.getFileUrl())
                .contractType(request.getContractType())
                .build();

        Contract savedContract = contractRepository.save(contract);
        return ContractResponse.from(savedContract);
    }

    // 계약서 수정
    public ContractResponse updateContract(Long contractId, Long memberId, ContractUpdateRequest request) {
        Contract contract = findContractByIdAndMemberId(contractId, memberId);

        contract.update(request.getTitle());

        return ContractResponse.from(contract);
    }

    // 계약서 삭제
    public void deleteContract(Long contractId, Long memberId) {
        Contract contract = findContractByIdAndMemberId(contractId, memberId);
        contractRepository.delete(contract);
    }

    // 즐겨찾기 등록
    public void bookmarkContract(Long contractId, Long memberId) {
        Contract contract = findContractByIdAndMemberId(contractId, memberId);
        contract.bookmark();
    }

    // 즐겨찾기 해제
    public void unbookmarkContract(Long contractId, Long memberId) {
        Contract contract = findContractByIdAndMemberId(contractId, memberId);
        contract.unbookmark();
    }

    private Contract findContractByIdAndMemberId(Long contractId, Long memberId) {
        return contractRepository.findByContractIdAndMemberId(contractId, memberId)
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));
    }
}
