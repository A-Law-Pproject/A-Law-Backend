package com.service.alaw.platform.contract.application.dto.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "FastAPI OCR 응답")
public record FastApiOcrResponse(
        @JsonProperty("success")
        boolean success,

        @JsonProperty("processing_time")
        double processingTime,

        @Schema(description = "S3 이미지 URL (Spring에서 주입)")
        @JsonProperty("image_url")
        String imageUrl,

        @JsonProperty("image_width")
        int imageWidth,

        @JsonProperty("image_height")
        int imageHeight,

        @JsonProperty("full_text")
        String fullText,

        @JsonProperty("markdown")
        String markdown,

        @JsonProperty("contract_data")
        Map<String, Object> contractData,

        @JsonProperty("validation")
        Map<String, Object> validation,

        @Schema(description = "단어별 OCR 결과 (좌표 포함)")
        @JsonProperty("words")
        List<OcrBlock> words,

        @JsonProperty("warnings")
        List<String> warnings,

        @JsonProperty("error")
        String error,

        @Schema(description = "마스킹된 S3 이미지 URL (개인정보 블러 처리)")
        @JsonProperty("masked_image_url")
        String maskedImageUrl,

        // Spring에서 주입 (FastAPI 응답에는 없는 필드)
        @Schema(description = "생성된 계약서 ID")
        @JsonProperty("contract_id")
        Long contractId,

        @Schema(description = "분석 작업 ID (SSE 구독에 사용)")
        @JsonProperty("job_id")
        String jobId
) {}
