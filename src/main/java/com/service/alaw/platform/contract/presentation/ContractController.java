package com.service.alaw.platform.contract.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.ocr.FastApiOcrResponse;
import com.service.alaw.platform.contract.application.service.ContractService;
import com.service.alaw.platform.contract.presentation.swagger.ContractSpec;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController implements ContractSpec {

  private final ContractService contractService;

  private static final Set<String> ALLOWED_CONTENT_TYPES =
      Set.of("image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf");

  @Override
  @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<FastApiOcrResponse>> processOcr(
      @RequestParam("file") MultipartFile imageFile, @CurrentUserId Long userId) {

    validateFile(imageFile);

    FastApiOcrResponse response = contractService.uploadAndOCR(imageFile, userId);
    return ApiResponse.success(response);
  }

  private void validateFile(MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("파일이 비어있습니다.");
    }

    if (!isValidImageType(file.getContentType())) {
      throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (지원 형식: JPG, PNG, GIF, WEBP, PDF)");
    }
  }

  private boolean isValidImageType(String contentType) {
    return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType);
  }
}
