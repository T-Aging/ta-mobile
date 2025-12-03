package com.example.tam.modules.main;

import com.example.tam.dto.ApiResponse;
import com.example.tam.modules.custom.CustomMenuService;
import com.example.tam.modules.order.OrderService;
import com.example.tam.modules.user.UserRepository;
import com.example.tam.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/main")
@RequiredArgsConstructor
public class MainController {

    private final QrCodeService qrCodeService;
    private final CustomMenuService customMenuService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping("/qr")
    public ResponseEntity<ApiResponse<QrCodeService.QrCodeResponse>> getMainQr(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        QrCodeService.QrCodeResponse qrCode = qrCodeService.generateUserQrCode(userId);
        return ResponseEntity.ok(ApiResponse.success(qrCode));
    }

    @GetMapping("/custom")
    public ResponseEntity<ApiResponse<?>> getMainCustom(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        var customMenus = customMenuService.getFavoriteMenus(userId);
        return ResponseEntity.ok(ApiResponse.success("메인 화면 추천 커스텀 메뉴", customMenus));
    }

    @GetMapping("/order")
    public ResponseEntity<ApiResponse<?>> getMainOrder(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        var orders = orderService.getAllOrders(userId);
        return ResponseEntity.ok(ApiResponse.success("현재 진행중인 주문", orders));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<?>> getMainUser(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return ResponseEntity.ok(ApiResponse.success("사용자 요약 정보", user));
    }
}
