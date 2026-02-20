package com.service.alaw.security.oauth2.handler;

import com.service.alaw.platform.user.domain.entity.User;
import com.service.alaw.security.jwt.application.dto.JwtTokenRequest;
import com.service.alaw.common.util.HttpUtil;
import com.service.alaw.security.jwt.application.util.JwtProvider;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final HttpUtil httpUtil;

    @Value("${auth.redirect.url}")
    private String REDIRECT_PATH;

    /*
        기존에 존재하는 유저의 경우, 토큰 발급을 진행합니다.
        리다이렉트 시킵니다.
     */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        try {
            /// 인증객체에서 User 가져오기
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            User user = principal.getUser();

            /// Access, Refresh 토큰 생성
            JwtTokenRequest tokenRequest = JwtTokenRequest.from(user);
            String accessToken = jwtProvider.createAccessToken(tokenRequest);
            String refreshToken = jwtProvider.createRefreshToken(tokenRequest);

            /// HTTP 쿠키 추가
            httpUtil.addAccessTokenCookie(httpServletResponse, accessToken);
            httpUtil.addRefreshTokenCookie(httpServletResponse, refreshToken);

            /// 시큐리티 홀더에 해당 멤버 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            /// 쿠키와 함께 리다이렉트 (프론트 마이페이지)
            getRedirectStrategy().sendRedirect(httpServletRequest, httpServletResponse, REDIRECT_PATH + "/mypage");
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 에러 발생", e);
            httpServletResponse.sendRedirect(REDIRECT_PATH + "/login?error=auth_failed");
        }
    }
}
