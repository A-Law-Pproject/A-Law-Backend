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

            // user
            new RequestInfo(GET, "/v1/user", null),     /// 임시 정보
            new RequestInfo(PATCH, "/v1/user", UserRole.User),      /// 추가
            new RequestInfo(POST, "/v1/user", null),    /// 회원가입

            // auth-dev
            new RequestInfo(POST, "/v1/auth/dev", null),    /// 개발용 토큰

            // auth
            new RequestInfo(DELETE, "/v1/auth", UserRole.User),     /// 로그아웃
            new RequestInfo(PUT, "/v1/auth", null),     /// 재발급
            new RequestInfo(GET, "/v1/auth", null),     /// 토큰 여부 체크

            // users
            new RequestInfo(DELETE, "/v1/users", UserRole.User),     /// 회원탈퇴
            new RequestInfo(GET, "/v1/users/mypage", UserRole.User),    /// 내 정보
            new RequestInfo(GET, "/v1/users/{userId}", UserRole.User),      /// 다른 정보
            new RequestInfo(GET, "/v1/users", null),      /// 레디스
            new RequestInfo(PATCH, "/v1/users", UserRole.User),         /// 수정
            new RequestInfo(POST, "/v1/users", null),       /// 회원가입

            // oauth2
            new RequestInfo(POST, "/api/v1/oauth2/**", null),

            // like
            new RequestInfo(POST, "/v1/likes/**", UserRole.User),
            new RequestInfo(DELETE, "/v1/likes/**", UserRole.User),

            // contracts
            new RequestInfo(GET, "/api/v1/contracts/**", UserRole.User),
            new RequestInfo(POST, "/api/v1/contracts/**", UserRole.User),
            new RequestInfo(PUT, "/api/v1/contracts/**", UserRole.User),
            new RequestInfo(DELETE, "/api/v1/contracts/**", UserRole.User),
            new RequestInfo(PATCH, "/api/v1/contracts/**", UserRole.User),

            // infra
            new RequestInfo(GET, "/v1/infra/**", null),


            // static resources
            new RequestInfo(GET, "/docs/**", null),
            new RequestInfo(GET, "/*.ico", null),
            new RequestInfo(GET, "/resources/**", null),
            new RequestInfo(GET, "/style.css", null),
            new RequestInfo(GET, "/index.html", null),
            new RequestInfo(GET, "/error", null),

            // Swagger UI 및 API 문서 관련 요청
            new RequestInfo(GET, "/v3/api-docs/**", null),
            new RequestInfo(GET, "/swagger-ui/**", null),
            new RequestInfo(GET, "/swagger-resources/**", null),
            new RequestInfo(GET, "/webjars/**", null),
            new RequestInfo(GET, "/swagger-ui.html", null),

            // 정적 아이콘 요청
            new RequestInfo(GET, "/favicon.ico", null),
            new RequestInfo(GET, "/apple-touch-icon.png", null)

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
