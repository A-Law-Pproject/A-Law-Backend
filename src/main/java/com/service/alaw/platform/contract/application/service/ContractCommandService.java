package com.service.alaw.platform.contract.application.service;

import com.service.alaw.platform.contract.application.dto.crud.ContractCreateRequest;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractUpdateRequest;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import com.service.alaw.platform.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContractCommandService {

  private final ContractRepository contractRepository;
  private final UserRepository userRepository;
  private final ContractValidator contractValidator;

  public ContractResponse createContract(Long userId, ContractCreateRequest request) {
    log.info("계약서 저장 확정 시작 - userId: {}, contractId: {}, title: {}", userId, request.contractId(), request.title());
    Contract contract = contractValidator.validateContractOwnership(request.contractId(), userId);
    contract.confirmSave(request.title(), request.contractType());
    log.info("계약서 저장 확정 완료 - contractId: {}", contract.getContractId());
    return ContractResponse.from(contract);
  }

  public ContractResponse updateContract(
      Long contractId, Long userId, ContractUpdateRequest request) {
    log.info("계약서 수정 시작 - contractId: {}, userId: {}", contractId, userId);
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);
    contract.updateTitle(request.title());
    log.info("계약서 수정 완료 - contractId: {}", contractId);
    return ContractResponse.from(contract);
  }

  public void deleteContract(Long contractId, Long userId) {
    log.info("계약서 삭제 시작 - contractId: {}, userId: {}", contractId, userId);
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);
    contractRepository.delete(contract);
    log.info("계약서 삭제 완료 - contractId: {}", contractId);
  }

  public void toggleBookmark(Long contractId, Long userId, boolean bookmark) {
    log.info("북마크 변경 시작 - contractId: {}, userId: {}, bookmark: {}", contractId, userId, bookmark);
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);

    if (bookmark) {
      contract.bookmark();
    } else {
      contract.unbookmark();
    }
    log.info("북마크 변경 완료 - contractId: {}", contractId);
  }

  public String saveRawText(Long contractId, Long userId, String textContent) {
    log.info("원문 텍스트 저장 시작 - contractId: {}, userId: {}, 텍스트 길이: {}", contractId, userId, textContent.length());
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);
    contract.updateRawText(textContent);
    log.info("원문 텍스트 저장 완료 - contractId: {}", contractId);
    return contract.getRawText();
  }

}
