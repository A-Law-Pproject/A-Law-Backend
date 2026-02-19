package com.service.alaw.platform.contract.application.dto.text;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "텍스트 저장 응답")
public record TextContentResponse(
    @Schema(description = "계약서 ID", example = "1") Long contractId,
    @Schema(description = "저장된 텍스트", example = "임대차 계약서\n제1조 (목적)...") String textContent) {
  public static TextContentResponse of(Long contractId, String textContent) {
    return new TextContentResponse(contractId, textContent);
  }
}
