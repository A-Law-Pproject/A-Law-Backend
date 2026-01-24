package com.service.alaw.security.jwt.domain.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

import static com.service.alaw.common.util.KeyUtil.getRefreshTokenKey;

@Getter
@RedisHash
public class JwtRefreshToken {

    @Id
    private String id;

    @Indexed
    private Long userId;

    @Indexed
    private String refreshToken;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private long expireTime;

    /// 빌더 생성자
    @Builder
    public JwtRefreshToken(Long userId, String refreshToken, long expireTime) {
        this.id = getRefreshTokenKey(userId);  /// RedisUtil 통해 Key 발급
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime;
    }

    /// 정적 팩토리 메소드
    public static JwtRefreshToken of(Long userId, String refreshToken, Long expireTime) {
        return JwtRefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expireTime(expireTime)
                .build();
    }

}
