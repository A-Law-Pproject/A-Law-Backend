package com.service.alaw.platform.contract.application.dto;

public record TermExplainRequest(
        String term,
        String contractId,
        String context,
        String surroundingText
) {

}
