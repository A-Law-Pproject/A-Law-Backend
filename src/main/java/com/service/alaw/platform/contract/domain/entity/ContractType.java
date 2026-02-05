package com.service.alaw.platform.contract.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContractType {
    LEASE("임대차계약서"),
    SALE("매매계약서"),
    JEONSE("전세계약서"),
    MONTHLY_RENT("월세계약서"),
    COMMERCIAL_LEASE("상가임대차계약서"),
    OTHER("기타");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ContractType fromLabel(String label) {
        for (ContractType type : values()) {
            if (type.label.equals(label) || type.name().equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ContractType label: " + label);
    }
}
