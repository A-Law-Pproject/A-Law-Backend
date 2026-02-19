package com.service.alaw.platform.contract.application.dto.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "OCR 결과 응답")
public record FastApiOcrResponse(
    @Schema(description = "작업 ID (비동기 처리 추적용)", example = "550e8400-e29b-41d4-a716-446655440000")
        @JsonProperty("job_id")
        String jobId,
    @Schema(description = "처리 상태", example = "ocr_complete") @JsonProperty("status") String status,
    @Schema(description = "S3 이미지 URL", example = "https://s3.amazonaws.com/bucket/image.png")
        @JsonProperty("image_url")
        String imageUrl,
    @Schema(description = "이미지 원본 너비 (픽셀)", example = "1920") @JsonProperty("image_width")
        Integer imageWidth,
    @Schema(description = "이미지 원본 높이 (픽셀)", example = "1080") @JsonProperty("image_height")
        Integer imageHeight,
    @Schema(description = "추출된 텍스트 블록 목록") @JsonProperty("text_blocks") List<TextBlock> textBlocks,
    @Schema(description = "전체 텍스트 (순서대로 결합)", example = "계약서 전체 내용...") @JsonProperty("full_text")
        String fullText) {}
