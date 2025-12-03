package com.example.tam.modules.push;

import com.example.tam.dto.PushDto;
import com.example.tam.modules.push.entity.PushHistory;
import com.example.tam.modules.user.UserRepository;
import com.example.tam.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {
    
    private final PushHistoryRepository pushHistoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PushDto.Response> getAllPushHistory(Long userId) {
        return pushHistoryRepository.findByUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PushDto.Response> getPushHistoryByType(Long userId, String type) {
        return pushHistoryRepository.findByUserIdAndType(userId, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void sendKioskConfirmation(Long userId, String orderNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        PushHistory push = PushHistory.builder()
                .user(user)
                .type("kiosk-confirm")
                .message("주문번호 " + orderNumber + "가 키오스크에서 확인되었습니다.")
                .build();

        pushHistoryRepository.save(push);
        log.info("키오스크 확인 푸시 전송 - userId: {}, orderNumber: {}", userId, orderNumber);
    }

    @Transactional
    public void sendReceiptArrival(Long userId, String receiptUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        PushHistory push = PushHistory.builder()
                .user(user)
                .type("receipt-arrived")
                .message("모바일 영수증이 도착했습니다.")
                .build();

        pushHistoryRepository.save(push);
        log.info("영수증 도착 푸시 전송 - userId: {}", userId);
    }

    private PushDto.Response mapToResponse(PushHistory push) {
        return PushDto.Response.builder()
                .id(push.getId())
                .type(push.getType())
                .title(getTitle(push.getType()))
                .message(push.getMessage())
                .isRead(false) // 추후 읽음 처리 로직 추가 필요
                .createdAt(push.getSentAt())
                .build();
    }

    private String getTitle(String type) {
        return switch (type) {
            case "kiosk-confirm" -> "키오스크 주문 확인";
            case "kakao-open-api" -> "카카오톡 알림";
            case "receipt-arrived" -> "모바일 영수증 도착";
            default -> "알림";
        };
    }
}
