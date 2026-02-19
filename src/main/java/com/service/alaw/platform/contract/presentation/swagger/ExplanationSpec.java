package com.service.alaw.platform.contract.presentation.swagger;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.explanation.EasyExplanationRequest;
import com.service.alaw.platform.contract.application.dto.explanation.EasyExplanationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Contract Explanation", description = "계약서 쉬운 말 요약 API")
public interface ExplanationSpec {

  @Operation(summary = "쉬운 말 요약 생성", description = "어려운 법률 문장을 쉬운 말로 변환합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "요약 생성 성공",
        content = @Content(schema = @Schema(implementation = EasyExplanationResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "인증되지 않은 사용자",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "서버 오류",
        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
  })
  ResponseEntity<ApiResponse<EasyExplanationResponse>> getEasyExplanation(
      @RequestBody EasyExplanationRequest request);
}
