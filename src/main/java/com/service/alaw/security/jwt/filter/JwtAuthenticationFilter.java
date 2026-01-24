package com.service.alaw.security.jwt.filter;

import com.service.alaw.common.util.HttpUtil;
import com.service.alaw.security.jwt.application.exception.JwtAuthenticationException;
import com.service.alaw.security.jwt.application.util.JwtValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * JWT 검증 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final JwtAuthenticationFailureHandler failureHandler;
    private final HttpUtil httpUtil;

    /// 필터 작동
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /// OPTIONS 필터에서 타지않도록 넣는다.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            /// 토큰 추출
            Optional<String> accessTokenOptional = httpUtil.getAccessToken(request);

            /// 토큰이 존재할 때만 인증 처리
            if (accessTokenOptional.isPresent()) {
                String accessToken = accessTokenOptional.get();

                /// 토큰 검증 후, Authentication 객체 반환
                Authentication authentication = jwtValidator.validateAccessToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            /// 토큰이 없거나, 인증에 성공했으면 다음 필터로 진행
            filterChain.doFilter(request, response);

        } catch (JwtAuthenticationException ex) {

            /// 인증 실패 시 핸들러 호출 후 필터 체인 중단
            failureHandler.commence(request, response, ex);
        }
    }
}
