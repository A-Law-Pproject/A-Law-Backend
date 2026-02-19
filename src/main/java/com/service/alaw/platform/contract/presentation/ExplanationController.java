package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.explanation.EasyExplanationRequest;
import com.service.alaw.platform.contract.application.dto.explanation.EasyExplanationResponse;
import com.service.alaw.platform.contract.application.service.ExplanationService;
import com.service.alaw.platform.contract.presentation.swagger.ExplanationSpec;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ExplanationController implements ExplanationSpec {

  private final ExplanationService explanationService;

  @Override
  @PostMapping("/easy-explanation")
  public ResponseEntity<ApiResponse<EasyExplanationResponse>> getEasyExplanation(
      @Valid @RequestBody EasyExplanationRequest request) {

    log.info(
        "쉬운 말 요약 요청 - contractId: {}, 원문 길이: {}",
        request.contractId(),
        request.originalSentence().length());

    EasyExplanationResponse response = explanationService.getEasyExplanation(request);
    return ApiResponse.success(response);
  }
}
