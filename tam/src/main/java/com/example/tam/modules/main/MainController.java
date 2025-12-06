package com.example.tam.modules.main;

import com.example.tam.dto.ApiResponse;
import com.example.tam.modules.custom.CustomService;
import com.example.tam.modules.order.OrderService;
import com.example.tam.modules.user.UserRepository;
import com.example.tam.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/main")
@RequiredArgsConstructor
@Tag(name = "메인 화면", description = "메인 화면용 통합 데이터 API")
public class MainController {

    private final UserService userService;
    private final CustomService customService; // CustomMenuService -> CustomService로 이름 통일 가정
    private final OrderService orderService;
    private final UserRepository userRepository;

    @Operation(summary = "메인: 내 QR 코드")
    @GetMapping("/qr")
    public ResponseEntity<ApiResponse<String>> getMainQr(Authentication auth) {
        Long userId = Long.parseLong(auth.getName()); // JWT Subject에서 ID 추출
        String qrCode = userService.getUserQr(userId.intValue());
        return ResponseEntity.ok(ApiResponse.success("메인 QR 조회 성공", qrCode));
    }

    @Operation(summary = "메인: 추천 커스텀 메뉴")
    @GetMapping("/custom")
    public ResponseEntity<ApiResponse<?>> getMainCustom(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        // CustomService에 getFavoriteMenus 혹은 getAllCustomMenus 등 적절한 메소드 호출
        var customMenus = customService.getAllCustomMenus(userId.intValue()); 
        return ResponseEntity.ok(ApiResponse.success("메인 추천 메뉴 조회 성공", customMenus));
    }

    @Operation(summary = "메인: 최근 주문 내역")
    @GetMapping("/order")
    public ResponseEntity<ApiResponse<?>> getMainOrder(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        // 최근 1건 혹은 3건 등 리미트 걸어서 조회하는 로직 권장
        var orders = orderService.getAllOrders(userId.intValue());
        return ResponseEntity.ok(ApiResponse.success("메인 최근 주문 조회 성공", orders));
    }

    @Operation(summary = "메인: 사용자 요약 정보")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<?>> getMainUser(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        var user = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return ResponseEntity.ok(ApiResponse.success("메인 사용자 정보 조회 성공", user));
    }
}