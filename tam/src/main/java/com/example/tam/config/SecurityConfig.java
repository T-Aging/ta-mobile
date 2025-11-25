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
            // CSRF 보안 설정 비활성화 (모바일 앱/API 서버 개발 시 보통 끕니다)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 요청 주소별 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 로그인 관련 주소는 누구나 접근 가능
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            );

        return http.build();
    }
}