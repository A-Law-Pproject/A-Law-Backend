package com.service.alaw.security.auth.application.service;


import com.service.alaw.common.exception.BaseException;
import com.service.alaw.common.exception.code.SecurityErrorCode;
import com.service.alaw.platform.user.domain.entity.User;
import com.service.alaw.platform.user.domain.repository.UserRepository;
import com.service.alaw.security.auth.application.usecase.AuthUseCase;
import com.service.alaw.security.jwt.application.dto.JwtTokenRequest;
import com.service.alaw.security.jwt.application.dto.JwtTokenResponse;
import com.service.alaw.security.jwt.application.util.JwtProvider;
import com.service.alaw.security.jwt.application.util.JwtValidator;
import com.service.alaw.security.jwt.domain.entity.JwtRefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    /// 유저 저장소
    private final UserRepository repository;

    /// 토큰 의존성
    private final JwtValidator jwtValidator;
    private final JwtProvider jwtProvider;

    // =================
    //  퍼블릭 로직
    // =================

    /// 로그아웃
    @Override
    @Transactional
    public void logout(Long userId, Optional<String> refreshToken) {

        /// DB 검증
        User user = repository.findById(userId)
                .orElseThrow(() -> new BaseException(SecurityErrorCode.NOT_FOUND_ID));

        /// 없다면 예외처리
        if (refreshToken.isEmpty()) {
            throw new BaseException(SecurityErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        /// 레디스에서 삭제하도록 로직 수행
        jwtValidator.removeRefreshToken(user.getUserId(), refreshToken.get());
    }

    /// 토큰 여부 및 검증성 체크
    @Override
    public boolean checkToken(Optional<String> optionalAccessToken) {

        /// 토큰이 없으면 false 반환
        if (optionalAccessToken.isEmpty()) {
            return false;
        }

        // 토큰이 있다면 검증 시도
        String jwtToken = optionalAccessToken.get();
        jwtValidator.validateAccessToken(jwtToken);

        /// 검증 성공 시 true 반환
        return true;
    }


    /// 토큰 재발급
    @Override
    @Transactional
    public JwtTokenResponse reissue(Optional<String> refreshToken) {

        /// 없다면 예외처리
        if (refreshToken.isEmpty()) {
            throw new BaseException(SecurityErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        /// 존재하는 리프레쉬 토큰 검증
        JwtRefreshToken token = jwtValidator.validateRefreshToken(refreshToken.get());

        /// 리프레쉬 토큰 바탕으로 조회
        User user = repository.findById(token.getUserId())
                .orElseThrow(() -> new BaseException(SecurityErrorCode.NOT_FOUND_ID));

        /// 인증된 유저에게 JWT 발급하기
        var jwtRequest = JwtTokenRequest.from(user);

        /// 기존 리프레쉬 토큰 무효화하기 (RDB)
        jwtValidator.removeRefreshToken(user.getUserId(), token.getRefreshToken());

        /// 새로운 액세스토큰/리프레쉬 토큰 발급
        String newAccessToken = jwtProvider.createAccessToken(jwtRequest);
        String newRefreshToken = jwtProvider.createRefreshToken(jwtRequest);

        return JwtTokenResponse.of(newAccessToken, newRefreshToken);
    }

}
