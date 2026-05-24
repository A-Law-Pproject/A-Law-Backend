package com.service.alaw.platform.contract.application.service;

import com.service.alaw.common.exception.ContractNotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class ContractValidator {

  private final ContractRepository contractRepository;

  public Contract validateContractOwnership(Long contractId, Long userId) {
    return contractRepository
        .findByContractIdAndUser_UserId(contractId, userId)
        .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));
  }

  public String validateAvailableTitleForCreate(Long userId, String title) {
    String normalizedTitle = normalizeTitle(title);
    if (!StringUtils.hasText(normalizedTitle)) {
      throw new IllegalArgumentException("계약서 이름은 비어 있을 수 없습니다.");
    }
    if (contractRepository.existsByUser_UserIdAndTitleIgnoreCaseAndUserSavedTrue(userId, normalizedTitle)) {
      throw new IllegalArgumentException("이미 같은 이름의 계약서가 있습니다.");
    }
    return normalizedTitle;
  }

  public String validateAvailableTitleForUpdate(Long contractId, Long userId, String title) {
    String normalizedTitle = normalizeTitle(title);
    if (!StringUtils.hasText(normalizedTitle)) {
      throw new IllegalArgumentException("계약서 이름은 비어 있을 수 없습니다.");
    }
    if (contractRepository.existsByUser_UserIdAndTitleIgnoreCaseAndUserSavedTrueAndContractIdNot(
        userId, normalizedTitle, contractId)) {
      throw new IllegalArgumentException("이미 같은 이름의 계약서가 있습니다.");
    }
    return normalizedTitle;
  }

  private String normalizeTitle(String title) {
    if (title == null) {
      return null;
    }
    return title.strip();
  }
}
