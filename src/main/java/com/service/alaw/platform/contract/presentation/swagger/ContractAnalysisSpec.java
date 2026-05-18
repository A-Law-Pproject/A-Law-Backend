package com.service.alaw.platform.contract.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Contract Analysis", description = "계약서 분석 결과 조회 API")
public interface ContractAnalysisSpec {

  @Operation(
      summary = "analysisId(jobId)로 계약서 분석 결과 조회",
      description =
          """
          계약서 상세 응답의 `analysisId` 값을 사용해 저장된 요약/리스크 분석 결과를 JSON으로 조회합니다.
          - COMPLETED: summary, riskAnalysis 포함
          - FAILED: errorMessage 포함
          - PENDING/PROCESSING: 상태만 반환
          """)
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content =
            @Content(schema = @Schema(implementation = ContractAnalysisDetailResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "해당 사용자의 분석 작업을 찾을 수 없음")
  })
  ResponseEntity<ApiResponse<ContractAnalysisDetailResponse>> getAnalysisDetail(
      @Parameter(hidden = true) @CurrentUserId Long userId,
      @Parameter(description = "계약서 분석 jobId (analysisId)", example = "0c5096b8-23ab-4cc1-8f1d-8195e177295e")
          @PathVariable String jobId);
}
