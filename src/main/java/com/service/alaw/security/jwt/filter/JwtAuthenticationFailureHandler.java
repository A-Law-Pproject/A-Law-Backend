package com.service.alaw.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.logging.HttpLogUtil;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.security.jwt.application.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.service.alaw.common.util.KeyUtil.HTTP_ERROR_401;


/**
 * JWT 인증 실패 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFailureHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final HttpLogUtil httpUtil;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        /// 기본 에러 코드
        ApiResponse<Object> apiResponse;

        /// JWT 예외인 경우
        if (authException instanceof JwtAuthenticationException jwtEx) {
            apiResponse = ApiResponse.error(jwtEx.getErrorCode());
        } else {
            apiResponse = ApiResponse.error(CommonErrorCode.UNAUTHORIZED);
        }

        /// 응답 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /// 로그 찍기
        httpUtil.logHttpRequest(request, HTTP_ERROR_401);

        /// JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
