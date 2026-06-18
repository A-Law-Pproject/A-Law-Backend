package com.service.alaw.platform.contract.application.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "계약서 원본 이미지 조회 응답")
public record ContractImageResponse(
        @Schema(description = "계약서 ID", example = "102") Long contractId,
        @Schema(description = "원본 이미지 URL") String imageUrl,
        @Schema(description = "이미지 너비 (px)") int imageWidth,
        @Schema(description = "이미지 높이 (px)") int imageHeight
) {
}
