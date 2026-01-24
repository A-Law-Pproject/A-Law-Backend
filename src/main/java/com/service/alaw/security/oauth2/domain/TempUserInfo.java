package com.service.alaw.security.oauth2.domain;

import lombok.*;

/**
 * 회원가입을 위한 리다이렉트
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TempUserInfo {
    private String socialId;
    private String social;
    private String email;
    private String username;
    private String imageUrl;

    /// 온보딩을 위한 정보를 포함하는 도메인
    public static TempUserInfo from (OAuth2UserInfo userInfo) {
        return TempUserInfo.builder()
                .social(userInfo.getProvider())
                .socialId(userInfo.getProviderId())
                .email(userInfo.getEmail())
                .username(userInfo.getUserName())
                .imageUrl(userInfo.getImageUrl())
                .build();
    }

}
