package com.service.alaw.common.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractErrorCode implements BaseCode {

    CONTRACT_NOT_FOUND(
        "C4041",
        "계약서를 찾을 수 없습니다."
    ),

    CONTRACT_FORBIDDEN(
        "C4031",
        "해당 계약서에 접근할 권한이 없습니다."
    );

    private final String code;
    private final String message;
}
