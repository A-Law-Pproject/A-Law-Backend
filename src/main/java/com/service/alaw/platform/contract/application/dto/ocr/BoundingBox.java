package com.service.alaw.platform.contract.application.dto.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "텍스트 경계 상자 좌표 (픽셀 단위)")
public record BoundingBox(
        @Schema(description = "왼쪽 상단 X 좌표", example = "100")
        @JsonProperty("x")
        Integer x,

        @Schema(description = "왼쪽 상단 Y 좌표", example = "50")
        @JsonProperty("y")
        Integer y,

        @Schema(description = "너비", example = "200")
        @JsonProperty("width")
        Integer width,

        @Schema(description = "높이", example = "30")
        @JsonProperty("height")
        Integer height
) {}
