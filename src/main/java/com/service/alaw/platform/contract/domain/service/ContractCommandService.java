package com.service.alaw.platform.contract.domain.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.contract.application.dto.ContractCreateRequest;
import com.service.alaw.platform.contract.application.dto.ContractResponse;
import com.service.alaw.platform.contract.application.dto.ContractUpdateRequest;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import com.service.alaw.platform.user.domain.entity.User;
import com.service.alaw.platform.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractCommandService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;

    // 내 문서에 저장하기 (계약서 생성)
    public ContractResponse createContract(Long userId, ContractCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));

        Contract contract = Contract.builder()
                .user(user)
                .title(request.getTitle())
                .fileUrl(request.getFileUrl())
                .contractType(request.getContractType())
                .build();

        Contract savedContract = contractRepository.save(contract);
        return ContractResponse.from(savedContract);
    }

    // 계약서 수정
    public ContractResponse updateContract(Long contractId, Long userId, ContractUpdateRequest request) {
        Contract contract = findContractByIdAndUserId(contractId, userId);

        contract.updateTitle(request.getTitle());

        return ContractResponse.from(contract);
    }

    // 계약서 삭제
    public void deleteContract(Long contractId, Long userId) {
        Contract contract = findContractByIdAndUserId(contractId, userId);
        contractRepository.delete(contract);
    }

    // 즐겨찾기 등록
    public void bookmarkContract(Long contractId, Long userId) {
        Contract contract = findContractByIdAndUserId(contractId, userId);
        contract.bookmark();
    }

    // 즐겨찾기 해제
    public void unbookmarkContract(Long contractId, Long userId) {
        Contract contract = findContractByIdAndUserId(contractId, userId);
        contract.unbookmark();
    }

    private Contract findContractByIdAndUserId(Long contractId, Long userId) {
        return contractRepository.findByContractIdAndUser_UserId(contractId, userId)
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));
    }
}
