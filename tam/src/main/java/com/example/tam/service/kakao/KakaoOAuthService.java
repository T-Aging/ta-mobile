package com.example.tam.service.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.tam.exception.KakaoAuthException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class KakaoOAuthService {
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-id:}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret:}")
    private String clientSecret;
    
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri:http://localhost:8080/t-age/login/oauth2/code/kakao}")
    private String redirectUri;
    
    private static final String AUTHORIZATION_URI = "https://kauth.kakao.com/oauth/authorize";
    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    
    private final WebClient webClient;
    
    public KakaoOAuthService() {
        this.webClient = WebClient.builder().build();
    }

    public String getAuthorizationUrl() {
        return String.format(
            "%s?client_id=%s&redirect_uri=%s&response_type=code",
            AUTHORIZATION_URI,
            clientId,
            redirectUri
        );
    }

    public KakaoTokenResponse getAccessToken(String code) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("code", code);

            KakaoTokenResponse response = webClient.post()
                    .uri(TOKEN_URI)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(params))
                    .retrieve()
                    .bodyToMono(KakaoTokenResponse.class)
                    .block();

            if (response == null || response.getAccessToken() == null) {
                throw new KakaoAuthException("카카오 액세스 토큰을 받지 못했습니다");
            }

            log.info("카카오 액세스 토큰 발급 성공");
            return response;

        } catch (WebClientResponseException e) {
            log.error("카카오 토큰 요청 실패: {}", e.getResponseBodyAsString());
            throw new KakaoAuthException("카카오 액세스 토큰 요청 실패", e);
        } catch (Exception e) {
            log.error("카카오 토큰 요청 중 오류 발생", e);
            throw new KakaoAuthException("카카오 인증 처리 실패", e);
        }
    }

    @Data
    public static class KakaoTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        
        @JsonProperty("token_type")
        private String tokenType;
        
        @JsonProperty("refresh_token")
        private String refreshToken;
        
        @JsonProperty("expires_in")
        private Integer expiresIn;
        
        @JsonProperty("refresh_token_expires_in")
        private Integer refreshTokenExpiresIn;
        
        @JsonProperty("scope")
        private String scope;
    }
}
