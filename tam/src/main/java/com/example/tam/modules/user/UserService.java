package com.example.tam.modules.user;

import com.example.tam.common.entity.User;
import com.example.tam.dto.UserDto;
import com.example.tam.common.entity.Kakao;
import com.example.tam.exception.ResourceNotFoundException; 
import com.example.tam.modules.auth.AuthService;
import com.example.tam.modules.auth.KakaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final KakaoRepository kakaoRepository; 
    private final AuthService authService;
    @Transactional(readOnly = true)
    public UserDto.UserResponse getUserInfo(Integer userId) {
        User user = userRepository.findById(userId)
                // 여기서 ResourceNotFoundException을 쓰려면 위에서 import가 되어 있어야 합니다.
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        
        return UserDto.UserResponse.builder()
                .id(Long.valueOf(user.getUserId()))
                .username(user.getUsername())
                .email(null) // 이메일 필드가 Entity에 없으면 null 처리
                .phone(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImage())
                .createdAt(user.getSignupDate())
                .lastLoginAt(null)
                .build();
    }

    @Transactional
    public UserDto.UserResponse updateUserInfo(Integer userId, UserDto.UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPhone() != null) user.setPhoneNumber(request.getPhone());
        
        userRepository.save(user);
        log.info("사용자 정보 수정 완료 - userId: {}", userId);
        
        return getUserInfo(userId);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        // 1. 카카오 연동 계정인지 확인
        Kakao kakaoUser = kakaoRepository.findByUserId(userId).orElse(null);

        // 2. 카카오 계정이면 연결 끊기 요청 (Unlink)
        if (kakaoUser != null && kakaoUser.getAccessToken() != null) {
            log.info("카카오 회원 탈퇴 진행 - Unlink 요청");
            authService.unlinkKakao(kakaoUser.getAccessToken());
            
            // 카카오 테이블 데이터도 명시적 삭제 (Cascade 설정이 없으면 수동 삭제 필요)
            kakaoRepository.delete(kakaoUser);
        }

        // 3. 내부 DB 회원 삭제
        userRepository.deleteById(userId);
        log.info("회원 탈퇴 처리 완료 (DB 삭제) - userId: {}", userId);
    }

    public String getUserQr(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        
        if (user.getUserQr() == null) {
            user.setUserQr("QR_" + userId);
            userRepository.save(user);
        }
        
        return user.getUserQr();
    }

    // [추가된 메서드] 전화번호 등록
    @Transactional
    public Integer registerPhone(Integer userId, UserDto.PhoneRegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));

        // 1. 전화번호 업데이트 (하이픈 제거 로직 추가)
        String rawPhone = request.getPhoneNumber();
        if (rawPhone != null) {
            String cleanPhone = rawPhone.replaceAll("-", "").trim();
            user.setPhoneNumber(cleanPhone);
        }

        // 2. 이름 업데이트 (입력된 경우에만)
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setUsername(request.getName());
        }
        
        userRepository.save(user);

        return user.getUserId();
    }
}