package com.service.alaw.common.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonSuccessCode implements BaseCode{

    // Success
    SUCCESS("200", "요청이 성공적으로 처리되었습니다", 200),
    RETRIEVED("200", "조회가 완료되었습니다", 200),
    UPDATED("200", "수정이 완료되었습니다", 200),
    DELETED("200", "삭제가 완료되었습니다", 200),
    CREATED("201", "생성이 완료되었습니다", 201);

    private final String code;
    private final String message;
    private final int httpCode;
}
