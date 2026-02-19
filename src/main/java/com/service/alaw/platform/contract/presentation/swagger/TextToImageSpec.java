package com.service.alaw.platform.contract.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.image.TextToImageRequest;
import com.service.alaw.platform.contract.application.dto.image.TextToImageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Contract Image", description = "계약서 텍스트→이미지 변환 API")
public interface TextToImageSpec {

  @Operation(summary = "텍스트→이미지 변환", description = "텍스트를 계약서 문서 이미지로 변환합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "변환 성공",
        content = @Content(schema = @Schema(implementation = TextToImageResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "인증되지 않은 사용자",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "계약서를 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "서버 오류",
        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
  })
  ResponseEntity<ApiResponse<TextToImageResponse>> convertTextToImage(
      @Parameter(description = "계약서 ID", required = true, example = "102") @PathVariable
          Long contractId,
      @RequestBody TextToImageRequest request,
      @Parameter(hidden = true) @CurrentUserId Long userId);
}
