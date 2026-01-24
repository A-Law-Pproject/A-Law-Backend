package com.service.alaw.common.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KeyUtil {

    /// 공통 키
    private static final String SEPARATOR = ":";
    private static final String DELIMITER = "-";

    /// 합쳐서 사용하는 키 목록

    /// OAUTH
    private static final String OAUTH2_TEMP_USER = "OAUTH2_TEMP_USER:";

    /// JWT
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ID_CLAIM = "user_id";
    public static final String ROLE_CLAIM = "role";

    /// 예외
    public static final String HTTP_ERROR_401 = "[HTTP_인증 실패]";
    public static final String HTTP_ERROR_403 = "[HTTP_인가 실패]";
    public static final String HTTP_REQ = "[HTTP_요청]";


    // =====================
    //  합쳐서 사용하는 키 목록
    // =====================

    /// 키 생성 함수
    public String generateOAuth2TempUserKey() {
        return OAUTH2_TEMP_USER + UUID.randomUUID();
    }

    public static String getRefreshTokenKey(Long userId) {
        return REFRESH_TOKEN + SEPARATOR + userId;
    }

}
