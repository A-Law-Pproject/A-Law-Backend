package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.text.TextContentRequest;
import com.service.alaw.platform.contract.application.dto.text.TextContentResponse;
import com.service.alaw.platform.contract.application.service.ContractCommandService;
import com.service.alaw.platform.contract.presentation.swagger.ContractTextSpec;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractTextController implements ContractTextSpec {

  private final ContractCommandService contractCommandService;

  @Override
  @PostMapping("/{contractId}/text")
  public ResponseEntity<ApiResponse<TextContentResponse>> saveText(
      @PathVariable Long contractId,
      @Valid @RequestBody TextContentRequest request,
      @CurrentUserId Long userId) {

    log.info("텍스트 저장 요청 - contractId: {}, userId: {}", contractId, userId);

    String savedText =
        contractCommandService.saveRawText(contractId, userId, request.textContent());
    TextContentResponse response = TextContentResponse.of(contractId, savedText);

    return ApiResponse.success(response);
  }
}
