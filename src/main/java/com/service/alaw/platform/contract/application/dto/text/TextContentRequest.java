package com.service.alaw.platform.contract.application.dto.text;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "텍스트 저장 요청")
public record TextContentRequest(
    @Schema(description = "OCR 추출 텍스트", example = "임대차 계약서\n제1조 (목적)...")
        @NotBlank(message = "텍스트 내용은 필수입니다.")
        String textContent) {}
