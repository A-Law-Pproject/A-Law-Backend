package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractListResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractUpdateRequest;
import com.service.alaw.platform.contract.application.service.ContractCommandService;
import com.service.alaw.platform.contract.application.service.ContractQueryService;
import com.service.alaw.platform.contract.application.service.ContractService;
import com.service.alaw.platform.contract.domain.entity.ContractType;
import com.service.alaw.platform.contract.presentation.swagger.ContractManagementSpec;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractManagementController implements ContractManagementSpec {

  private static final Set<String> ALLOWED_CONTENT_TYPES =
      Set.of("image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf");

  private final ContractCommandService contractCommandService;
  private final ContractQueryService contractQueryService;
  private final ContractService contractService;

  // 계약서 파일 업로드 + OCR + 저장 (한 번에 처리)
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ContractResponse>> createContract(
      @CurrentUserId Long userId,
      @RequestParam("file") MultipartFile file,
      @RequestParam("title") String title,
      @RequestParam(value = "contractType", required = false) ContractType contractType) {

    if (file.isEmpty()) throw new IllegalArgumentException("파일이 비어있습니다.");
    if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType()))
      throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (지원 형식: JPG, PNG, GIF, WEBP, PDF)");

    ContractResponse response = contractService.uploadAndSave(file, title, contractType, userId);
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
