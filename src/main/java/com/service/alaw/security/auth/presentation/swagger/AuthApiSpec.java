package com.service.alaw.security.auth.presentation.swagger;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "인증 API", description = "로그아웃/액세스 토큰 재발급을 수행하는 API입니다")
public interface AuthApiSpec {

    /// 로그아웃
    @Operation(
            summary = "로그아웃 API",
            description = "이미 인증된 유저가 로그아웃하는 API"
    )
    ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal PrincipalDetails customUserDetails);

    /// 토큰 재발급
    @Operation(
            summary = "액세스토큰 재발급 API",
            description = "액세스 토큰을 재발급받는 API"
    )
    ResponseEntity<ApiResponse<Void>> reissue(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );


    /// 액세스 토큰 여부 체크
    @Operation(
            summary = "액세스토큰 여부 체크 API",
            description = "액세스 토큰이 존재하는지 체크하는 API"
    )
    ResponseEntity<ApiResponse<Boolean>> checkAccessToken(HttpServletRequest httpServletRequest);
}
