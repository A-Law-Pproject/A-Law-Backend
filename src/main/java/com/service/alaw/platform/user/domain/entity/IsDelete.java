package com.service.alaw.platform.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IsDelete {
    Y("탈퇴"),
    N("정상");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static IsDelete fromLabel(String label) {
        for (IsDelete value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown IsDelete label: " + label);
    }

    public boolean isDeleted() {
        return this == Y;
    }
}
