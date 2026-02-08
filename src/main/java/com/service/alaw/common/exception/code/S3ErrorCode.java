package com.service.alaw.common.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseCode {

    // S3 관련 에러 코드
    AWS_S3_UPLOAD_FAIL("S3_001", "AWS S3 파일 업로드에 실패했습니다."),
    INVALID_FILE_FORMAT("S3_002", "잘못된 파일 형식입니다."),
    FILE_SIZE_EXCEEDED("S3_003", "파일 크기가 제한을 초과했습니다."),
    NOT_FOUND_FILE("S3_004", "해당 파일을 찾을 수 없습니다."),
    AWS_S3_DELETE_FAIL("S3_005", "S3 파일 삭제 중 오류가 발생했습니다."),
    EMPTY_FILE("S3_006", "업로드할 파일이 비어있습니다.");

    private final String code;
    private final String message;
}
