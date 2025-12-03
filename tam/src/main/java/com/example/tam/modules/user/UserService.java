package com.example.tam.modules.user;

import com.example.tam.common.entity.User;
import com.example.tam.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto.UserResponse getUserInfo(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        return UserDto.UserResponse.builder()
                .id(Long.valueOf(user.getUserId()))
                .username(user.getUsername())
                .email(null)
                .phone(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImage())
                .createdAt(user.getSignupDate())
                .lastLoginAt(null)
                .build();
    }

    @Transactional
    public UserDto.UserResponse updateUserInfo(Integer userId, UserDto.UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPhone() != null) user.setPhoneNumber(request.getPhone());
        
        userRepository.save(user);
        log.info("사용자 정보 수정 완료 - userId: {}", userId);
        
        return getUserInfo(userId);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
        log.info("회원 탈퇴 처리 완료 - userId: {}", userId);
    }

    public String getUserQr(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        if (user.getUserQr() == null) {
            user.setUserQr("QR_" + userId);
            userRepository.save(user);
        }
        
        return user.getUserQr();
    }
}
