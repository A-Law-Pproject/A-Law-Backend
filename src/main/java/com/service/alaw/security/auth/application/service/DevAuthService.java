package com.service.alaw.security.auth.application.service;

import com.service.alaw.common.exception.BaseException;
import com.service.alaw.common.exception.code.SecurityErrorCode;
import com.service.alaw.platform.user.domain.entity.User;
import com.service.alaw.platform.user.domain.entity.UserRole;
import com.service.alaw.platform.user.domain.repository.UserRepository;
import com.service.alaw.security.jwt.application.dto.JwtTokenRequest;
import com.service.alaw.security.jwt.application.dto.JwtTokenResponse;
import com.service.alaw.security.jwt.application.util.JwtProvider;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DevAuthService {

    /// 유저 저장
    private final UserRepository repository;

    /// 토큰 발급
    private final JwtProvider tokenProvider;

    /// 개발자용 ID
    private final Long devUserId = 1L;

    // =================
    //  퍼블릭 로직
    // =================

    /// 개발용 토큰 생성
    @Transactional
    public JwtTokenResponse devCreate() {

        /// 테스트용 유저 정보 수정
        User user;

        /// 유저 생성하기
        if (repository.existsById(devUserId)) {
            user = repository.findById(devUserId)
                    .orElseThrow(() -> new BaseException(SecurityErrorCode.NOT_FOUND_ID));
        } else {
            User dev = User.builder()
                    .name("DevUser")
                    .role(UserRole.ADMIN)
                    .profileImage(null)
                    .build();
            user = repository.save(dev);
        }

        /// 인증필터에 적용
        PrincipalDetails principalDetails = PrincipalDetails.of(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        /// 토큰 발급하기
        var tokenRequest = JwtTokenRequest.from(user);
        String devAccessToken = tokenProvider.createDevAccessToken(tokenRequest);
        String refreshToken = tokenProvider.createRefreshToken(tokenRequest);

        /// 리턴
        return JwtTokenResponse.of(devAccessToken, refreshToken);
    }

}
