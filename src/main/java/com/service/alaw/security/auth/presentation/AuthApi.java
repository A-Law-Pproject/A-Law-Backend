package com.service.alaw.security.auth.presentation;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.security.auth.application.usecase.AuthUseCase;
import com.service.alaw.security.auth.presentation.swagger.AuthApiSpec;
import com.service.alaw.security.jwt.application.dto.JwtTokenResponse;
import com.service.alaw.common.util.HttpUtil;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApi implements AuthApiSpec {

    private final AuthUseCase service;

    /// HTTP 서비스
    private final HttpUtil httpUtil;

    // =================
    //  퍼블릭 로직
    // =================

    /**
     * 로그아웃
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 리프레쉬 토큰 까보기
        Optional<String> refreshToken = httpUtil.getRefreshToken(httpServletRequest);

        /// 서비스 로직 실행
        service.logout(principalDetails.getId(), refreshToken);

        /// 쿠키 삭제하기
        httpUtil.removeAccessTokenCookie(httpServletResponse);
        httpUtil.removeRefreshTokenCookie(httpServletResponse);
        httpUtil.removeLoginFlagCookie(httpServletResponse);

        /// 리턴
        return ApiResponse.deleted();
    }

    /**
     * 토큰 재발급
     */
    @PutMapping
    public ResponseEntity<ApiResponse<Void>> reissue(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {

        /// 리프레쉬 토큰 까보기
        Optional<String> refreshToken = httpUtil.getRefreshToken(httpServletRequest);

        /// 서비스 로직 실행
        JwtTokenResponse response = service.reissue(refreshToken);

        /// 토큰 재발급하기 (액세스/리프레쉬)
        httpUtil.addAccessTokenCookie(httpServletResponse, response.accessToken());
        httpUtil.addRefreshTokenCookie(httpServletResponse, response.refreshToken());


        /// 리턴
        return ApiResponse.updated();
    }

    /**
     * 토큰 여부 판단
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<Boolean>> checkAccessToken(HttpServletRequest httpServletRequest) {

        /// 토큰 있는지 체크
        Optional<String> accessToken = httpUtil.getAccessToken(httpServletRequest);

        /// 서비스
        var response = service.checkToken(accessToken);

        /// 리턴
        return ApiResponse.success(response);
    }

}
