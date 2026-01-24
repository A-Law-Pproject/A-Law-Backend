package com.service.alaw.security.oauth2.domain;

public interface OAuth2UserInfo {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getUserName();
    String getImageUrl();

}
