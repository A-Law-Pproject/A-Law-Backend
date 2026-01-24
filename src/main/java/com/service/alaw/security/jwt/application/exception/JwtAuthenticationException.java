package com.service.alaw.security.jwt.application.exception;

import org.springframework.security.core.AuthenticationException;
import com.service.alaw.common.exception.code.BaseCode;
import lombok.Getter;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final BaseCode errorCode;

    /// 생성자
    public JwtAuthenticationException(BaseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
