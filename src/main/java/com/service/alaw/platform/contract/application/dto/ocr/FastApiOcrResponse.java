package com.service.alaw.platform.contract.application.dto.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "OCR 결과 응답")
public record FastApiOcrResponse(
        @Schema(description = "처리 성공 여부", example = "true")
        @JsonProperty("success")
        Boolean success,

        @Schema(description = "처리 시간 (초)", example = "2.5")
        @JsonProperty("processing_time")
        Double processingTime,

        @Schema(description = "이미지 원본 너비 (픽셀)", example = "1200")
        @JsonProperty("image_width")
        Integer imageWidth,

        @Schema(description = "이미지 원본 높이 (픽셀)", example = "1700")
        @JsonProperty("image_height")
        Integer imageHeight,

        @Schema(description = "전체 텍스트 (순서대로 결합)", example = "임대차 계약서...")
        @JsonProperty("full_text")
        String fullText,

        @Schema(description = "텍스트 블록 목록 (좌표는 % 단위)")
        @JsonProperty("blocks")
        List<OcrBlock> blocks
) {}
