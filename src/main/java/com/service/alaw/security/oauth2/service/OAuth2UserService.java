package com.service.alaw.security.oauth2.service;

import com.service.alaw.common.exception.BaseException;
import com.service.alaw.common.exception.code.SecurityErrorCode;
import com.service.alaw.platform.user.domain.entity.User;
import com.service.alaw.platform.user.domain.repository.UserRepository;
import com.service.alaw.security.oauth2.domain.OAuth2UserInfo;
import com.service.alaw.security.oauth2.domain.PrincipalDetails;
import com.service.alaw.security.oauth2.domain.TempUserInfo;
import com.service.alaw.security.oauth2.domain.kakao.KakaoUserInfo;
import com.service.alaw.security.oauth2.handler.SignupRequiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * 소셜 로그인 유저 가져오기
     * @param userRequest   정보 요청
     * @return              유저
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        /// 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        /// resistrationId 가져오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        /// OAuth2를 바탕으로 정보 생성
        OAuth2UserInfo userInfo = createOAuth2User(registrationId, oAuth2UserAttributes);

        // 이메일로 기존 유저 조회 (소셜 로그인 연동 로직 필요시 수정)
        Optional<User> existUser = userRepository.findByName(userInfo.getUserName());

        /// 존재한다면 로그인
        if (existUser.isPresent()) {
            ///  이미 존재하는 유저를 반환한다.
            User user = existUser.get();
            return PrincipalDetails.of(user, oAuth2UserAttributes);
        } else {
            /// 기존에 유저가 없다면, 실패 핸들러로 예외 던지기
            var temp = TempUserInfo.from(userInfo);
            throw new SignupRequiredException(temp);
        }

    }


    /**
     * 소셜로그인 유저 생성
     * @param registrationId        소셜로그인 종류
     * @param oAuth2UserAttributes  소셜로그인 정보
     */
    private OAuth2UserInfo createOAuth2User(String registrationId, Map<String, Object> oAuth2UserAttributes) {
        OAuth2UserInfo userInfo;

        switch (registrationId.toUpperCase()) {
            case "KAKAO":
                userInfo = new KakaoUserInfo(oAuth2UserAttributes);
                break;
            default:
                throw new BaseException(SecurityErrorCode.BAD_REQUEST_OAUTH2);
        }

        return userInfo;
    }

}
