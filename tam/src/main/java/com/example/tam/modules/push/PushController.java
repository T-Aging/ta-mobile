package com.example.tam.modules.push;

import com.example.tam.modules.push.entity.PushHistory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/t-age/users/{userid}/push") // 경로를 명확히 하기 위해 /push 추가 권장 (명세상 /search 중복 주의)
@RequiredArgsConstructor
public class PushController {

    private final PushHistoryRepository pushHistoryRepository;

    // 키오스크 주문 확인, 카카오톡 알림, 영수증 도착 알림 조회
    // 명세에는 모두 GET /t-age/users/{userid}/search 로 되어 있어 구분 필요
    @GetMapping("/history")
    public ResponseEntity<?> getPushHistory(@PathVariable Long userid, @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(pushHistoryRepository.findByUserIdAndType(userid, type));
        }
        return ResponseEntity.ok(pushHistoryRepository.findByUserId(userid));
    }
}
