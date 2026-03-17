package com.service.alaw.platform.contract.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractCreateRequest;
import com.service.alaw.platform.contract.application.dto.crud.ContractListResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractResponse;
import com.service.alaw.platform.contract.application.dto.crud.ContractUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Contract Management", description = "계약서 관리 API")
public interface ContractManagementSpec {

  @Operation(summary = "계약서 생성", description = "새로운 계약서를 생성하여 내 문서함에 저장합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "계약서 생성 성공",
        content = @Content(schema = @Schema(implementation = ContractResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (유효성 검증 실패)"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "인증 실패")
  })
  ResponseEntity<ApiResponse<ContractResponse>> createContract(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @Valid @RequestBody ContractCreateRequest request);

  @Operation(summary = "계약서 상세 조회", description = "계약서 ID로 특정 계약서의 상세 정보를 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = @Content(schema = @Schema(implementation = ContractResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "계약서를 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "접근 권한 없음 (본인의 계약서가 아님)")
  })
  ResponseEntity<ApiResponse<ContractResponse>> getContract(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @Parameter(description = "계약서 ID", example = "1") @PathVariable("id") Long contractId);

  @Operation(summary = "내 계약서 목록 조회", description = "현재 로그인한 사용자의 모든 계약서 목록을 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = @Content(schema = @Schema(implementation = ContractListResponse.class)))
  })
  ResponseEntity<ApiResponse<List<ContractListResponse>>> getMyContracts(
      @Parameter(hidden = true) @CurrentUserId Long userId);

  @Operation(summary = "계약서 삭제", description = "계약서를 삭제합니다. 본인의 계약서만 삭제할 수 있습니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "204",
        description = "삭제 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "계약서를 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "접근 권한 없음")
  })
  ResponseEntity<ApiResponse<Void>> deleteContract(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @Parameter(description = "계약서 ID", example = "1") @PathVariable("id") Long contractId);

  @Operation(summary = "계약서 수정", description = "계약서의 세부 정보를 수정합니다. 본인의 계약서만 수정할 수 있습니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "수정 성공",
        content = @Content(schema = @Schema(implementation = ContractResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "계약서를 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "접근 권한 없음")
  })
  ResponseEntity<ApiResponse<ContractResponse>> updateContract(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @Parameter(description = "계약서 ID", example = "1") @PathVariable("id") Long contractId,
      @Valid @RequestBody ContractUpdateRequest request);
}
