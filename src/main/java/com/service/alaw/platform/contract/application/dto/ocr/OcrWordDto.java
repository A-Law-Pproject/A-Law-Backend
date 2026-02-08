package com.service.alaw.platform.contract.application.dto.ocr;

public record OcrWordDto(
        String text,
        Double left,
        Double top,
        Double width,
        Double height,
        Double confidence
) {
}
