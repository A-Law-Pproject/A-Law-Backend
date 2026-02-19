package com.service.alaw.platform.contract.application.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "텍스트→이미지 변환 응답")
public record TextToImageResponse(
    @Schema(description = "계약서 ID", example = "102") @JsonProperty("contract_id") Long contractId,
    @Schema(description = "처리 상태", example = "completed") @JsonProperty("status") String status,
    @Schema(description = "변환 결과") @JsonProperty("transcription_result")
        TranscriptionResult transcriptionResult,
    @Schema(description = "생성된 이미지 URL", example = "https://s3.amazonaws.com/bucket/image.png")
        @JsonProperty("image_url")
        String imageUrl,
    @Schema(description = "수정 일시", example = "2026-01-08T13:00:00") @JsonProperty("updated_at")
        LocalDateTime updatedAt) {

  public static TextToImageResponse of(
      Long contractId,
      String status,
      TranscriptionResult transcriptionResult,
      String imageUrl,
      LocalDateTime updatedAt) {
    return new TextToImageResponse(contractId, status, transcriptionResult, imageUrl, updatedAt);
  }
}
