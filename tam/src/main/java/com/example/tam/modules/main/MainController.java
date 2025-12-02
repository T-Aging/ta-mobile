package com.example.tam.modules.main;

import com.example.tam.common.QrService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/t-age")
@RequiredArgsConstructor
public class TAgeMainController {

    private final QrService qrService;

    // --- 메인 화면 ---

    @GetMapping("/main/qr")
    public ResponseEntity<?> getMainQr() {
        // 메인 화면용 공통 QR 혹은 로그인 유저의 QR
        return ResponseEntity.ok(Map.of("qrData", "main-qr-code-data"));
    }

    @GetMapping("/main/custom")
    public ResponseEntity<?> getMainCustom() {
        return ResponseEntity.ok("메인 화면 추천 커스텀 메뉴 데이터");
    }

    @GetMapping("/main/order")
    public ResponseEntity<?> getMainOrder() {
        return ResponseEntity.ok("메인 화면 현재 진행중인 주문 정보");
    }

    @GetMapping("/main/user")
    public ResponseEntity<?> getMainUser() {
        return ResponseEntity.ok("메인 화면 유저 요약 정보");
    }

    // --- 키오스크 QR (회원 QR 조회) ---
    @GetMapping("/users/{userid}/qr")
    public ResponseEntity<?> getUserQr(@PathVariable Long userid) {
        try {
            // 유저 ID를 기반으로 QR 생성
            String qrImage = qrService.generateQrCode("User:" + userid, 200, 200);
            return ResponseEntity.ok(Map.of("qrImage", "data:image/png;base64," + qrImage));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
