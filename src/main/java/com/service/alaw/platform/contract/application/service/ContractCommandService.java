package com.service.alaw.platform.contract.application.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.contract.application.dto.crud.ContractCreateRequest;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractUpdateRequest;
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
    private final ContractValidator contractValidator;

    public ContractResponse createContract(Long userId, ContractCreateRequest request) {
        User user = findUserById(userId);
        Contract contract = buildContract(user, request);
        Contract savedContract = contractRepository.save(contract);

        return ContractResponse.from(savedContract);
    }

    public ContractResponse updateContract(Long contractId, Long userId, ContractUpdateRequest request) {
        Contract contract = contractValidator.validateContractOwnership(contractId, userId);
        contract.updateTitle(request.title());

        return ContractResponse.from(contract);
    }

    public void deleteContract(Long contractId, Long userId) {
        Contract contract = contractValidator.validateContractOwnership(contractId, userId);
        contractRepository.delete(contract);
    }

    public void toggleBookmark(Long contractId, Long userId, boolean bookmark) {
        Contract contract = contractValidator.validateContractOwnership(contractId, userId);

        if (bookmark) {
            contract.bookmark();
        } else {
            contract.unbookmark();
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));
    }

    private Contract buildContract(User user, ContractCreateRequest request) {
        return Contract.builder()
                .user(user)
                .title(request.title())
                .fileUrl(request.fileUrl())
                .contractType(request.contractType())
                .build();
    }
}
