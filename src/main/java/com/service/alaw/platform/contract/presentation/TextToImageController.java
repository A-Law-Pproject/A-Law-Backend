package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.image.TextToImageRequest;
import com.service.alaw.platform.contract.application.dto.image.TextToImageResponse;
import com.service.alaw.platform.contract.application.service.TextToImageService;
import com.service.alaw.platform.contract.presentation.swagger.TextToImageSpec;
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
public class TextToImageController implements TextToImageSpec {

  private final TextToImageService textToImageService;

  @Override
  @PostMapping("/{contractId}/image")
  public ResponseEntity<ApiResponse<TextToImageResponse>> convertTextToImage(
      @PathVariable Long contractId,
      @Valid @RequestBody TextToImageRequest request,
      @CurrentUserId Long userId) {

    log.info(
        "텍스트→이미지 변환 요청 - contractId: {}, userId: {}, 텍스트 길이: {}",
        contractId,
        userId,
        request.textContent().length());

    TextToImageResponse response =
        textToImageService.convertTextToImage(contractId, userId, request);
    return ApiResponse.success(response);
  }
}
