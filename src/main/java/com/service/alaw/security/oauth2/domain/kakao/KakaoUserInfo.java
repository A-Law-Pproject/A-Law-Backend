package com.service.alaw.security.oauth2.domain.kakao;

import com.service.alaw.security.oauth2.domain.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> account;
    private final Map<String, Object> profile;

    @SuppressWarnings("unchecked")
    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.account = Optional.ofNullable(attributes.get("kakao_account"))
                .filter(Map.class::isInstance)
                .map(m -> (Map<String, Object>) m)
                .orElse(Collections.emptyMap());

        this.profile = Optional.ofNullable(account.get("profile"))
                .filter(Map.class::isInstance)
                .map(m -> (Map<String, Object>) m)
                .orElse(Collections.emptyMap());
    }

    @Override
    public String getProvider() {
        return "KAKAO";
    }

    @Override
    public String getProviderId() {
        return  Optional.ofNullable(attributes.get("id"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalStateException("Kakao user id is required"));
    }

    @Override
    public String getEmail() {
        return Optional.ofNullable(account.get("email"))
                .map(Object::toString)
                .orElse("No email provided");
    }

    @Override
    public String getUserName() {
        return Optional.ofNullable(profile.get("nickname"))
                .map(Object::toString)
                .orElse("No name provided");
    }

    @Override
    public String getImageUrl() {

        return Optional.ofNullable(profile.get("profile_image_url"))
                .map(Object::toString)
                .orElse("No image provided");
    }

}
