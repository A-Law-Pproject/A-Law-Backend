package com.service.alaw.platform.contract.application.dto.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OCR 텍스트 블록 (좌표는 % 단위)")
public record OcrBlock(
        @Schema(description = "인식된 텍스트", example = "임대차 계약서")
        @JsonProperty("text")
        String text,

        @Schema(description = "인식 신뢰도 (0~1)", example = "0.95")
        @JsonProperty("confidence")
        Double confidence,

        @Schema(description = "좌측 상단 X 좌표 (%)", example = "10.5")
        @JsonProperty("x")
        Double x,

        @Schema(description = "좌측 상단 Y 좌표 (%)", example = "5.2")
        @JsonProperty("y")
        Double y,

        @Schema(description = "블록 너비 (%)", example = "80.0")
        @JsonProperty("width")
        Double width,

        @Schema(description = "블록 높이 (%)", example = "3.5")
        @JsonProperty("height")
        Double height
) {}
