package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractCreateRequest;
import com.service.alaw.platform.contract.application.dto.crud.ContractListResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractUpdateRequest;
import com.service.alaw.platform.contract.application.service.ContractCommandService;
import com.service.alaw.platform.contract.application.service.ContractQueryService;
import com.service.alaw.platform.contract.presentation.swagger.ContractManagementSpec;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractManagementController implements ContractManagementSpec {

  private final ContractCommandService contractCommandService;
  private final ContractQueryService contractQueryService;

  // 내 문서에 저장하기 (계약서 생성)
  @PostMapping
  public ResponseEntity<ApiResponse<ContractResponse>> createContract(
      @CurrentUserId Long userId, @Valid @RequestBody ContractCreateRequest request) {

    ContractResponse response = contractCommandService.createContract(userId, request);
    return ApiResponse.created(response);
  }

  // 계약서 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ContractResponse>> getContract(
      @CurrentUserId Long userId, @PathVariable("id") Long contractId) {

    ContractResponse contract = contractQueryService.getContract(contractId, userId);
    return ApiResponse.retrieved(contract);
  }

  // 계약서 목록 조회
  @GetMapping
  public ResponseEntity<ApiResponse<List<ContractListResponse>>> getMyContracts(
      @CurrentUserId Long userId) {

    List<ContractListResponse> contracts = contractQueryService.getMyContracts(userId);
    return ApiResponse.retrieved(contracts);
  }

  // 계약서 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteContract(
      @CurrentUserId Long userId, @PathVariable("id") Long contractId) {

    contractCommandService.deleteContract(contractId, userId);
    return ApiResponse.deleted();
  }

  // 계약서 수정
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<ContractResponse>> updateContract(
      @CurrentUserId Long userId,
      @PathVariable("id") Long contractId,
      @Valid @RequestBody ContractUpdateRequest request) {

    ContractResponse response = contractCommandService.updateContract(contractId, userId, request);
    return ApiResponse.updated(response);
  }
}
