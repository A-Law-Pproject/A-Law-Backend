package com.service.alaw.platform.contract.application.dto;

public record ContractRequest(
        String text,
        String contractId,
        String userId
) {
}
