package com.service.alaw.platform.contract.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContractStatus {
    PENDING("분석 대기"),
    PROCESSING("분석 중"),
    COMPLETED("분석 완료"),
    FAILED("분석 실패");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ContractStatus fromLabel(String label) {
        for (ContractStatus status : values()) {
            if (status.label.equals(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ContractStatus label: " + label);
    }
}
