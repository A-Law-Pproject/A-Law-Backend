package com.service.alaw.platform.contract.application.dto.explanation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "쉬운 말 요약 요청")
public record EasyExplanationRequest(
    @Schema(description = "계약서 ID", example = "1") Long contractId,
    @Schema(description = "설명할 법률 용어", example = "전대")
        @NotBlank(message = "법률 용어는 필수입니다.")
        String term,
    @Schema(description = "원문 문장", example = "임차인은 임대인의 서면 동의 없이 본 부동산을 전대하거나 임차권을 양도할 수 없다.")
        @NotBlank(message = "원문 문장은 필수입니다.")
        String originalSentence) {}
