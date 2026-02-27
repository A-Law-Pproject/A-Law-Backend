package com.service.alaw.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.service.alaw.common.util.KeyUtil.ACCESS_TOKEN;
import static com.service.alaw.common.util.KeyUtil.REFRESH_TOKEN;


@Component
public class HttpUtil {

    @Value("${auth.jwt.access.expiration}")
    private long accessExpiration;

    @Value("${auth.jwt.access.dev_expiration}")
    private long devExpiration;

    @Value("${auth.jwt.refresh.expiration}")
    private long refreshExpiration;

    @Value("${auth.cookie.secureOption}")
    private boolean secureOption;

    @Value("${auth.cookie.sameSiteOption}")
    private String sameSiteOption;

    @Value("${auth.cookie.pathOption}")
    private String cookiePathOption;

    @Value("${auth.cookie.domain}")
    private String cookieDomain;


    /// 액세스 토큰 쿠키 가져오기
    public Optional<String> getAccessToken(HttpServletRequest request) {
        return extractToken(request, ACCESS_TOKEN);
    }

    /// 리프레쉬 토큰 쿠키 가져오기
    public Optional<String> getRefreshToken(HttpServletRequest request) {
        return extractToken(request, REFRESH_TOKEN);
    }

    /// 액세스 토큰을 쿠키에 저장하기
    public void addAccessTokenCookie(HttpServletResponse httpServletResponse, String accessToken) {

        /// 쿠키 생성 및 저장
        createCookie(httpServletResponse, ACCESS_TOKEN, accessToken, accessExpiration);

    }

    /// 개발용 액세스 토큰을 쿠키에 저장하기
    public void addDevAccessTokenCookie(HttpServletResponse httpServletResponse, String devAccessToken) {

        /// 쿠키 생성 및 저장
        createCookie(httpServletResponse, ACCESS_TOKEN, devAccessToken, devExpiration);

    }

    /// 리프레쉬 토큰을 쿠키에 저장하기
    public void addRefreshTokenCookie(HttpServletResponse httpServletResponse, String refreshToken) {

        /// 쿠키 생성 및 저장
        createCookie(httpServletResponse, REFRESH_TOKEN, refreshToken, refreshExpiration);
    }

    /// 로그인 flag 쿠키 추가 (프론트에서 읽기 가능)
    public void addLoginFlagCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("is_logged_in", "true")
                .maxAge(accessExpiration)
                .path(cookiePathOption)
                .httpOnly(false)
                .secure(secureOption)
                .sameSite(sameSiteOption);

        if (cookieDomain != null && !cookieDomain.equals("localhost")) {
            builder.domain(cookieDomain);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    /// 로그인 flag 쿠키 삭제
    public void removeLoginFlagCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("is_logged_in", "")
                .maxAge(0)
                .path(cookiePathOption)
                .httpOnly(false)
                .secure(secureOption)
                .sameSite(sameSiteOption);

        if (cookieDomain != null && !cookieDomain.equals("localhost")) {
            builder.domain(cookieDomain);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    /// 액세스 토큰을 삭제하기
    public void removeAccessTokenCookie(HttpServletResponse httpServletResponse) {
        deleteCookie(httpServletResponse, ACCESS_TOKEN);
    }

    /// 리프레쉬 토큰을 삭제하기
    public void removeRefreshTokenCookie(HttpServletResponse httpServletResponse) {
        deleteCookie(httpServletResponse, REFRESH_TOKEN);
    }

    /// 요청자의 정보를 헤더에서 조회하기 위한 함수
    public HeaderInfo getClientInfo(HttpServletRequest request) {

        /// IP
        String ip = getClientIp(request);

        /// 메서드
        String httpMethod = request.getMethod();

        /// 요청 주소
        String uri = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);

        /// 요청자
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "익명";

        return new HeaderInfo(ip, httpMethod, uri, username);
    }

    /// 헤더의 값을 전달하기 위해서 레코드 클래스 생성
    public record HeaderInfo(String ip, String httpMethod, String uri, String userName) {

    }


    // =================
    //  내부 공통 함수
    // =================

    /// 쿠키 생성하기
    private void createCookie(HttpServletResponse response, String cookieName, String cookieValue, long maxAge) {

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, cookieValue)
                .maxAge(maxAge)
                .path(cookiePathOption)
                .httpOnly(true)
                .secure(secureOption)
                .sameSite(sameSiteOption);

        if (cookieDomain != null && !cookieDomain.equals("localhost")) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }

    /// 쿠키에서 토큰 가져오기
    private Optional<String> extractToken(HttpServletRequest httpServletRequest, String type) {

        /// 쿠키 가져오기
        Cookie[] cookies = httpServletRequest.getCookies();

        /// 쿠키가 존재한다면,
        if (cookies != null) {
            for (Cookie cookie : cookies) {

                /// 해당 타입의 쿠키만 추출
                if (cookie.getName().equals(type)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    /// 공통 쿠키 삭제 메서드
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path(cookiePathOption)
                .secure(secureOption)
                .httpOnly(true)
                .sameSite(sameSiteOption);

        if (cookieDomain != null && !cookieDomain.equals("localhost")) {
            builder.domain(cookieDomain);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    /// 요청자의 실제 IP를 조회하기 위한 함수
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        /// X-Forwarded-For이 있다면
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 여러 개라면 첫 번째 값이 클라이언트 IP
            return ip.split(",")[0].trim();
        }

        /// X-Forwarded-For이 없다면
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
