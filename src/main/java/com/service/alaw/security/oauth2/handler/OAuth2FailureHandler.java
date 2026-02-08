package com.service.alaw.security.oauth2.handler;

import com.service.alaw.common.util.KeyUtil;
import com.service.alaw.security.oauth2.domain.TempUserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /// 레디스 의존성 도입
    private final RedisTemplate<String, Object> redisTemplate;
    private final KeyUtil keyUtil;

    @Value("${cors.front.local}")
    public String REDIRECT_PATH;

    /**
     * 실패 핸들러 예외 처리
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        /// SignupRequiredException 예외처리 받았으면 실행
        if (exception instanceof SignupRequiredException) {

            /// 레디스로 회원가입 키 생성
            String tempUserKey = keyUtil.generateOAuth2TempUserKey();

            /// 임시 유저 처리
            TempUserInfo userInfo = ((SignupRequiredException) exception).getUserInfo();

            /// 레디스에 값 추가( 유효시간 5분 )
            redisTemplate.opsForValue().set(tempUserKey, userInfo, Duration.ofMinutes(5));
            redisTemplate.opsForValue().set(tempUserKey, userInfo, Duration.ofMinutes(5));

            String extraInfoUrl = REDIRECT_PATH + "/signup?state=" + tempUserKey;

            /// 응답을 리다이렉트
            response.sendRedirect(extraInfoUrl);
        } else {
            response.sendRedirect(REDIRECT_PATH + "/login?error");
        }
    }
}
