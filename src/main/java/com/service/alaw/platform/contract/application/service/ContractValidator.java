package com.service.alaw.platform.contract.application.service;

import com.service.alaw.common.exception.ContractNotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractValidator {

  private final ContractRepository contractRepository;

  public Contract validateContractOwnership(Long contractId, Long userId) {
    return contractRepository
        .findByContractIdAndUser_UserId(contractId, userId)
        .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.CONFLICT));
  }
}
