package com.service.alaw.platform.contract.application.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "텍스트→이미지 변환 요청")
public record TextToImageRequest(
    @Schema(description = "변환할 텍스트 내용", example = "제 1조 (목적) 본 계약은 갑과 을 사이의...")
        @NotBlank(message = "텍스트 내용은 필수입니다.")
        String textContent) {}
