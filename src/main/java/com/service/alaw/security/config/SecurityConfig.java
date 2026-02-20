package com.service.alaw.security.config;

import com.service.alaw.security.jwt.filter.JwtAuthenticationDeniedHandler;
import com.service.alaw.security.jwt.filter.JwtAuthenticationFailureHandler;
import com.service.alaw.security.jwt.filter.JwtAuthenticationFilter;
import com.service.alaw.security.jwt.filter.RequestMatcherHolder;
import com.service.alaw.platform.user.domain.entity.UserRole;
import com.service.alaw.security.oauth2.handler.OAuth2FailureHandler;
import com.service.alaw.security.oauth2.handler.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import com.service.alaw.security.oauth2.CookieOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security 설정 클래스
 *
 * 애플리케이션의 인증 및 권한 부여 정책을 정의한다.
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // OAuth2 관련 빈은 application.yml에서 OAuth2 설정 활성화 후 주입
     private final OAuth2UserService oAuth2UserService;
     private final OAuth2SuccessHandler oAuth2SuccessHandler;
     private final OAuth2FailureHandler oAuth2FailureHandler;

    /// JWT 관련
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationFailureHandler jwtFailureHandler;
    private final JwtAuthenticationDeniedHandler jwtDeniedHandler;

    /// 시큐리티 및 CORS
    private final RequestMatcherHolder requestMatcherHolder;
    private final CorsConfigurationSource corsConfigurationSource;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // 인증 불필요 (누구나 접근 가능)
                    requestMatcherHolder.getRequestInfoByMinRole(null)
                            .forEach(info -> auth.requestMatchers(info.method(), info.pattern()).permitAll());

                    // User 권한 필요
                    requestMatcherHolder.getRequestInfoByMinRole(UserRole.User)
                            .forEach(info -> auth.requestMatchers(info.method(), info.pattern())
                                    .hasAnyAuthority(UserRole.ADMIN.getRole(), UserRole.User.getRole()));

                    // Admin 권한 필요
                    requestMatcherHolder.getRequestInfoByMinRole(UserRole.ADMIN)
                            .forEach(info -> auth.requestMatchers(info.method(), info.pattern())
                                    .hasAnyAuthority(UserRole.ADMIN.getRole()));

                    auth.anyRequest().authenticated();
                })
                 .oauth2Login(oauth2 -> oauth2
                         .authorizationEndpoint(auth -> auth
                                 .authorizationRequestRepository(authorizationRequestRepository())
                         )
                         .userInfoEndpoint(userInfo -> userInfo
                                 .userService(oAuth2UserService)
                         )
                         .successHandler(oAuth2SuccessHandler)
                         .failureHandler(oAuth2FailureHandler)
                 )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(jwtFailureHandler)
                            .accessDeniedHandler(jwtDeniedHandler);
                });


        return http.build();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new CookieOAuth2AuthorizationRequestRepository();
    }
}
