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
        String error
) {}
