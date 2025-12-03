package com.example.tam.service.kakao;

import com.example.tam.exception.KakaoAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class KakaoApiService {
    
    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    
    private final WebClient webClient;
    
    public KakaoApiService() {
        this.webClient = WebClient.builder().build();
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            KakaoUserInfo userInfo = webClient.get()
                    .uri(KAKAO_USER_INFO_URI)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(KakaoUserInfo.class)
                    .block();

            if (userInfo == null || userInfo.getId() == null) {
                throw new KakaoAuthException("카카오 사용자 정보를 가져올 수 없습니다");
            }

            log.info("카카오 사용자 정보 조회 성공 - kakaoId: {}", userInfo.getId());
            return userInfo;

        } catch (WebClientResponseException e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getResponseBodyAsString());
            throw new KakaoAuthException("카카오 사용자 정보 조회 실패", e);
        } catch (Exception e) {
            log.error("카카오 API 호출 중 오류 발생", e);
            throw new KakaoAuthException("카카오 인증 처리 실패", e);
        }
    }
}
