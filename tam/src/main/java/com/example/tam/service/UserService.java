package com.example.tam.service;

import com.example.tam.dto.UserDto;
import com.example.tam.entity.User;
import com.example.tam.exception.ResourceNotFoundException;
import com.example.tam.exception.UnauthorizedException;
import com.example.tam.repository.UserRepository;
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
    public UserDto.UserResponse getUserInfo(Long userId, Long requestUserId) {
        if (!userId.equals(requestUserId)) {
            throw new UnauthorizedException("본인의 정보만 조회할 수 있습니다");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        
        return mapToUserResponse(user);
    }

    @Transactional
    public UserDto.UserResponse updateUserInfo(
            Long userId, 
            Long requestUserId, 
            UserDto.UserUpdateRequest request) {
        
        if (!userId.equals(requestUserId)) {
            throw new UnauthorizedException("본인의 정보만 수정할 수 있습니다");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmail(request.getEmail()) 
                    && !request.getEmail().equals(user.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user.setPhone(request.getPhone());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("사용자 정보 수정 완료 - userId: {}", userId);
        
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId, Long requestUserId) {
        if (!userId.equals(requestUserId)) {
            throw new UnauthorizedException("본인의 계정만 탈퇴할 수 있습니다");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        
        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);
        
        log.info("회원 탈퇴 처리 완료 - userId: {}", userId);
    }

    @Transactional(readOnly = true)
    public boolean existsUser(Long userId) {
        return userRepository.existsById(userId);
    }

    private UserDto.UserResponse mapToUserResponse(User user) {
        return UserDto.UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
