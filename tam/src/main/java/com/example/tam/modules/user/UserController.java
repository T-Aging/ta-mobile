package com.example.tam.modules.user;

import com.example.tam.modules.user.entity.User; // 기존 User 엔티티 사용
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/t-age")
@RequiredArgsConstructor
public class TAgeUserController {

    private final UserRepository userRepository;
    // private final KakaoAuthService kakaoAuthService; // 카카오 통신용 서비스(별도 구현 필요)

    // 1. 카카오 회원 가입
    @GetMapping("/signup")
    public ResponseEntity<?> kakaoSignup(@RequestParam String code) {
        // TODO: 카카오 인가 코드로 액세스 토큰 발급 -> 사용자 정보 조회 -> DB 저장
        return ResponseEntity.ok("카카오 회원가입 완료 (Mock)");
    }

    // 2. 카카오 소셜 로그인
    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> loginData) {
        // TODO: 카카오 토큰 검증 및 자체 JWT 발급
        return ResponseEntity.ok(Map.of("token", "jwt-token-example", "userId", 1L));
    }

    // --- 마이 페이지 ---

    // 사용자 정보 조회
    @GetMapping("/users/{userid}/search")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userid) {
        User user = userRepository.findById(userid).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // 사용자 정보 수정
    @PutMapping("/users/{userid}/update")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long userid, @RequestBody User userDetails) {
        User user = userRepository.findById(userid).orElseThrow();
        user.setUsername(userDetails.getUsername()); // 예시
        userRepository.save(user);
        return ResponseEntity.ok("수정 완료");
    }

    // 로그아웃
    @PostMapping("/users/{userid}/logout")
    public ResponseEntity<?> logout(@PathVariable Long userid) {
        return ResponseEntity.ok("로그아웃 처리됨");
    }

    // 탈퇴하기
    @DeleteMapping("/users/{userid}/logout")
    public ResponseEntity<?> withdraw(@PathVariable Long userid) {
        userRepository.deleteById(userid);
        return ResponseEntity.ok("탈퇴 완료");
    }
}
