package com.service.alaw.security.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class SecretKeyConfig {

    @Value("${auth.jwt.secret}")
    private String base64Secret;

    /**
     * Bean을 통해 비밀키 초기화
     */
    @Bean
    public SecretKey jwtSigningKey() {

        ///  Base64로 인코딩되어있는 문자열을 디코딩
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);

        /// 디코딩한 바이트 코드들을 시크릿 키로 변환
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
