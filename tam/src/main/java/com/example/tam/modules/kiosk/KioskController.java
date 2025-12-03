package com.example.tam.modules.kiosk;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.AuthDto;
import com.example.tam.modules.auth.AuthService;
import com.example.tam.modules.menu.MenuRepository; // 전체 메뉴 조회용
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/kiosk")
@RequiredArgsConstructor
@Tag(name = "키오스크", description = "키오스크 전용 API")
public class KioskController {

    private final AuthService authService;
    private final MenuRepository menuRepository;

    @Operation(summary = "QR 로그인 (키오스크)", description = "앱에서 생성한 QR 코드를 스캔하여 사용자 정보를 확인하고 토큰을 발급받습니다.")
    @PostMapping("/auth/qr")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> loginByQr(@RequestBody String qrCode) {
        // QR 코드 문자열(예: "QR_101")을 받아 사용자 검증 및 토큰 발급
        AuthDto.LoginResponse response = authService.loginByQrCode(qrCode);
        return ResponseEntity.ok(ApiResponse.success("키오스크 로그인 성공", response));
    }

    @Operation(summary = "전체 메뉴 데이터 동기화 (AI 학습용/키오스크 캐싱용)")
    @GetMapping("/menus")
    public ResponseEntity<ApiResponse<?>> getAllMenusForKiosk() {
        var menus = menuRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("전체 메뉴 데이터 조회 성공", menus));
    }
}
