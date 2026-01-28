package com.service.alaw.common.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode implements BaseCode {

    // JWT Access Token
    ACCESS_TOKEN_EXPIRED("AUTH_001", "액세스 토큰이 만료되었습니다"),
    ACCESS_TOKEN_SIGNATURE("AUTH_002", "액세스 토큰 서명이 유효하지 않습니다"),
    ACCESS_TOKEN_MALFORMED("AUTH_003", "액세스 토큰 형식이 올바르지 않습니다"),
    ACCESS_TOKEN_UNSUPPORTED("AUTH_004", "지원하지 않는 액세스 토큰입니다"),
    ACCESS_TOKEN_NOT_FOUND("AUTH_005", "액세스 토큰이 없습니다"),
    ACCESS_TOKEN_INVALID("AUTH_006", "유효하지 않은 액세스 토큰입니다"),
    ACCESS_TOKEN_NOT_USER("AUTH_007", "액세스 토큰의 사용자를 찾을 수 없습니다"),

    // JWT Refresh Token
    REFRESH_TOKEN_EXPIRED("AUTH_011", "리프레시 토큰이 만료되었습니다"),
    REFRESH_TOKEN_INVALID("AUTH_012", "유효하지 않은 리프레시 토큰입니다"),
    REFRESH_TOKEN_UNSUPPORTED("AUTH_013", "지원하지 않는 리프레시 토큰입니다"),
    REFRESH_TOKEN_NOT_FOUND("AUTH_014", "리프레시 토큰이 없습니다"),
    REFRESH_TOKEN_NOT_USER("AUTH_015", "리프레시 토큰의 사용자를 찾을 수 없습니다"),
    REFRESH_TOKEN_LOGOUT("AUTH_016", "로그아웃 처리 중 오류가 발생했습니다"),

    // OAuth2
    BAD_REQUEST_OAUTH2("AUTH_021", "지원하지 않는 OAuth2 제공자입니다"),

    // User
    NOT_FOUND_ID("AUTH_031", "사용자를 찾을 수 없습니다");

    private final String code;
    private final String message;
}
