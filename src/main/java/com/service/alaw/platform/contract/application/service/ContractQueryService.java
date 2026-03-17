package com.service.alaw.platform.contract.application.service;

import com.service.alaw.platform.contract.application.dto.crud.ContractListResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractQueryService {

  private final ContractRepository contractRepository;
  private final ContractValidator contractValidator;

  public List<ContractListResponse> getMyContracts(Long userId) {
    log.info("계약서 목록 조회 시작 - userId: {}", userId);
    List<Contract> contracts = contractRepository.findByUser_UserIdOrderByCreatedDateDesc(userId);
    log.info("계약서 목록 조회 완료 - userId: {}, 건수: {}", userId, contracts.size());
    return convertToListResponses(contracts);
  }

  public ContractResponse getContract(Long contractId, Long userId) {
    log.info("계약서 단건 조회 시작 - contractId: {}, userId: {}", contractId, userId);
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);
    log.info("계약서 단건 조회 완료 - contractId: {}", contractId);
    return ContractResponse.from(contract);
  }

  private List<ContractListResponse> convertToListResponses(List<Contract> contracts) {
    return contracts.stream().map(ContractListResponse::from).toList();
  }
}
