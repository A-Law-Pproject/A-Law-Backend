package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.analysis.ContractAnalysisDetailResponse;
import com.service.alaw.platform.contract.application.service.ContractAnalysisQueryService;
import com.service.alaw.platform.contract.presentation.swagger.ContractAnalysisSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractAnalysisController implements ContractAnalysisSpec {

  private final ContractAnalysisQueryService contractAnalysisQueryService;

  @GetMapping("/analysis/{jobId}")
  public ResponseEntity<ApiResponse<ContractAnalysisDetailResponse>> getAnalysisDetail(
      @CurrentUserId Long userId, @PathVariable String jobId) {
    ContractAnalysisDetailResponse response =
        contractAnalysisQueryService.getAnalysisDetail(jobId, userId);
    return ApiResponse.retrieved(response);
  }
}
