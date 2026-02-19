package com.service.alaw.platform.contract.application.dto.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경계 상자 좌표")
public record BoundingBox(
    @Schema(description = "X 좌표", example = "100") @JsonProperty("x") Integer x,
    @Schema(description = "Y 좌표", example = "200") @JsonProperty("y") Integer y,
    @Schema(description = "너비", example = "300") @JsonProperty("width") Integer width,
    @Schema(description = "높이", example = "50") @JsonProperty("height") Integer height) {}
