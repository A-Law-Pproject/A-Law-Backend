package com.service.alaw.platform.contract.application.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 변환 결과")
public record TranscriptionResult(
    @Schema(description = "원본 텍스트", example = "제 1조 (목적) 본 계약은 갑과 을 사이의...")
        @JsonProperty("raw_text")
        String rawText,
    @Schema(description = "변환 신뢰도 점수", example = "0.98")
        @JsonProperty("confidence_score")
        Double confidenceScore,
    @Schema(description = "페이지 수", example = "3") @JsonProperty("page_count") Integer pageCount) {

  public static TranscriptionResult of(String rawText, Double confidenceScore, Integer pageCount) {
    return new TranscriptionResult(rawText, confidenceScore, pageCount);
  }
}
