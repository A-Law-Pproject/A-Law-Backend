package com.service.alaw.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.logging.HttpLogUtil;
import com.service.alaw.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.service.alaw.common.util.KeyUtil.HTTP_ERROR_403;


/**
 * JWT 인가 실패 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final HttpLogUtil httpUtil;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        /// 권한 부족 403 Error
        ApiResponse<Object> apiResponse = ApiResponse.error(CommonErrorCode.FORBIDDEN);

        /// response 제작
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /// 로그 찍기
        httpUtil.logHttpRequest(request, HTTP_ERROR_403);

        /// JSON 응답
        objectMapper.writeValue(response.getWriter(), apiResponse);

    }
}
