package com.service.alaw.security.jwt.filter;

import com.service.alaw.platform.user.domain.entity.UserRole;
import org.springframework.lang.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Component
public class RequestMatcherHolder {

    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(

            // 공통
            new RequestInfo(OPTIONS, "/**", null),
            new RequestInfo(GET, "/", null),
            new RequestInfo(GET, "/login", null),
            new RequestInfo(GET, "/error", null),

            // OAuth2 (Spring Security 기본 경로)
            new RequestInfo(GET, "/oauth2/authorization/**", null),
            new RequestInfo(GET, "/login/oauth2/code/**", null),

            // auth
            new RequestInfo(POST, "/api/v1/auth/dev", null),
            new RequestInfo(DELETE, "/api/v1/auth", UserRole.User),
            new RequestInfo(PUT, "/api/v1/auth", null),
            new RequestInfo(GET, "/api/v1/auth", null),

            // users
            new RequestInfo(POST, "/api/v1/users", null),
            new RequestInfo(GET, "/api/v1/users", null),
            new RequestInfo(GET, "/api/v1/users/mypage", UserRole.User),
            new RequestInfo(GET, "/api/v1/users/{userId}", UserRole.User),
            new RequestInfo(PATCH, "/api/v1/users", UserRole.User),
            new RequestInfo(DELETE, "/api/v1/users", UserRole.User),

            // contracts
            new RequestInfo(POST, "/api/v1/contracts/ocr", UserRole.User),
            new RequestInfo(GET, "/api/v1/contracts/**", UserRole.User),
            new RequestInfo(POST, "/api/v1/contracts/**", UserRole.User),
            new RequestInfo(PUT, "/api/v1/contracts/**", UserRole.User),
            new RequestInfo(DELETE, "/api/v1/contracts/**", UserRole.User),
            new RequestInfo(PATCH, "/api/v1/contracts/**", UserRole.User),

            // chatbot
            new RequestInfo(POST, "/api/v1/chat", UserRole.User),

            // likes
            new RequestInfo(POST, "/api/v1/likes/**", UserRole.User),
            new RequestInfo(DELETE, "/api/v1/likes/**", UserRole.User),

            // infra
            new RequestInfo(GET, "/api/v1/infra/**", null),

            // Actuator
            new RequestInfo(GET, "/actuator/**", null),

            // Swagger UI 및 API 문서
            new RequestInfo(GET, "/v3/api-docs/**", null),
            new RequestInfo(GET, "/swagger-ui/**", null),
            new RequestInfo(GET, "/swagger-resources/**", null),
            new RequestInfo(GET, "/webjars/**", null),

            // OCR 테스트 페이지 (local 프로파일에서만 컨트롤러 활성화됨)
            new RequestInfo(GET, "/test/ocr", null),
            new RequestInfo(POST, "/test/ocr", null),

            // 정적 리소스
            new RequestInfo(GET, "/docs/**", null),
            new RequestInfo(GET, "/resources/**", null),
            new RequestInfo(GET, "/favicon.ico", null),
            new RequestInfo(GET, "/apple-touch-icon.png", null),
            new RequestInfo(GET, "/ocr-overlay.html", null),
            new RequestInfo(GET, "/sse-test.html", null)

    );

    /**
     * 최소 권한에 해당하는 요청 정보 리스트 반환
     * @param minRole 최소 권한 (Nullable)
     * @return 요청 정보 리스트
     */
    public List<RequestInfo> getRequestInfoByMinRole(@Nullable UserRole minRole) {
        return REQUEST_INFO_LIST.stream()
                .filter(reqInfo -> reqInfo.minRole() == minRole)
                .toList();
    }

    public record RequestInfo(HttpMethod method, String pattern, UserRole minRole) {}

}
