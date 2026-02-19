package com.service.alaw.platform.contract.application.service;

import com.service.alaw.platform.contract.application.dto.crud.ContractListResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractQueryService {

  private final ContractRepository contractRepository;
  private final ContractValidator contractValidator;

  public List<ContractListResponse> getMyContracts(Long userId) {
    List<Contract> contracts = contractRepository.findByUser_UserIdOrderByCreatedDateDesc(userId);
    return convertToListResponses(contracts);
  }

  public ContractResponse getContract(Long contractId, Long userId) {
    Contract contract = contractValidator.validateContractOwnership(contractId, userId);
    return ContractResponse.from(contract);
  }

  private List<ContractListResponse> convertToListResponses(List<Contract> contracts) {
    return contracts.stream().map(ContractListResponse::from).toList();
  }
}
