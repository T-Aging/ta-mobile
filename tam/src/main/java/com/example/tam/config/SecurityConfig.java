package com.example.tam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보안 설정 비활성화 (개발용)
            .csrf(AbstractHttpConfigurer::disable)
            // .httpBasic(AbstractHttpConfigurer::disable)
            // .formLogin(AbstractHttpConfigurer::disable)
            // 요청 주소별 권한 설정
            .authorizeHttpRequests(auth -> auth
                // [수정] "/t-age/**" 경로를 추가하여 로그인 관련 API를 모두 허용합니다.
                .requestMatchers("/api/auth/**", "/t-age/**", "/inter/ta-kiosk/auth/**", "/ws/kiosk/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            );

        return http.build();
    }
}