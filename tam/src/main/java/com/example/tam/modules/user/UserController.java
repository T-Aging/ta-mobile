package com.example.tam.modules.user;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.UserDto;
import com.example.tam.modules.auth.AuthService;
import com.example.tam.modules.qr.QrService; // [추가] QrService 임포트
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/users/{userid}")
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "마이 페이지 및 회원 관리 API")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final QrService qrService; // [추가] 서비스 주입

    // ... (기존 메서드들 생략, 아래 getUserQr만 수정) ...
    @Operation(summary = "사용자 정보 조회 (마이페이지)")
    @GetMapping
    public ResponseEntity<ApiResponse<UserDto.UserResponse>> getUserInfo(@PathVariable Integer userid) {
        UserDto.UserResponse response = userService.getUserInfo(userid);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", response));
    }

    @Operation(summary = "사용자 정보 수정")
    @PutMapping
    public ResponseEntity<ApiResponse<UserDto.UserResponse>> updateUserInfo(
            @PathVariable Integer userid,
            @Valid @RequestBody UserDto.UserUpdateRequest request) {
        UserDto.UserResponse response = userService.updateUserInfo(userid, request);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 수정 성공", response));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> withdraw(@PathVariable Integer userid) {
        userService.deleteUser(userid);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 성공", null));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@PathVariable Integer userid) {
        authService.logout(userid);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
    }

    @Operation(summary = "키오스크용 회원 QR 코드 데이터 조회")
    @GetMapping("/qr")
    public ResponseEntity<ApiResponse<String>> getUserQr(@PathVariable Integer userid) {
        // 1. "QR_1" 같은 문자열 데이터 가져오기
        String qrData = userService.getUserQr(userid);
        
        try {
            // 2. 문자열을 진짜 QR 코드 이미지(Base64)로 변환!
            String qrImage = qrService.generateQrCode(qrData, 300, 300);
            return ResponseEntity.ok(ApiResponse.success("QR 이미지 생성 성공", qrImage));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("QR 생성 실패"));
        }
    }
}