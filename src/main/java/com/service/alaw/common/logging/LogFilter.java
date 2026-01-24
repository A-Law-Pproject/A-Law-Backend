package com.service.alaw.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.service.alaw.common.util.KeyUtil.HTTP_REQ;


/**
 * HTTP 요청에 대한 필터를 작성합니다.
 * 디스패처 서블릿 앞에서 필터를 작성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class LogFilter extends OncePerRequestFilter {

    private final HttpLogUtil httpUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /// 로그 찍기
        httpUtil.logHttpRequest(request, HTTP_REQ);

        /// 로그 남기고 넘기기
        filterChain.doFilter(request, response);

    }
}
