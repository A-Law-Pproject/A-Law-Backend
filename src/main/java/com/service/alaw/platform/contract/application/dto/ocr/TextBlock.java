package com.service.alaw.platform.contract.application.dto.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "텍스트 블록 정보")
public record TextBlock(
        @Schema(description = "블록 ID", example = "1")
        @JsonProperty("id")
        Integer id,

        @Schema(description = "텍스트 내용", example = "임대차 계약서")
        @JsonProperty("text")
        String text,

        @Schema(description = "신뢰도 (0.0 ~ 1.0)", example = "0.95")
        @JsonProperty("confidence")
        Double confidence,

        @Schema(description = "경계 상자 좌표")
        @JsonProperty("bounding_box")
        BoundingBox boundingBox
) {}
