package com.example.tam.modules.user;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.UserDto;
import com.example.tam.service.AuthService;
import com.example.tam.service.QrCodeService;
import com.example.tam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/users/{userid}")
@RequiredArgsConstructor
@Tag(name = "사용자", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final QrCodeService qrCodeService;

    @Operation(summary = "사용자 정보 조회")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserDto.UserResponse>> getUserInfo(
            @PathVariable Long userid,
            Authentication auth) {
        
        Long requestUserId = (Long) auth.getPrincipal();
        UserDto.UserResponse response = userService.getUserInfo(userid, requestUserId);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", response));
    }

    @Operation(summary = "사용자 정보 수정")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserDto.UserResponse>> updateUserInfo(
            @PathVariable Long userid,
            @Valid @RequestBody UserDto.UserUpdateRequest request,
            Authentication auth) {
        
        Long requestUserId = (Long) auth.getPrincipal();
        UserDto.UserResponse response = userService.updateUserInfo(userid, requestUserId, request);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 수정 성공", response));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @PathVariable Long userid,
            Authentication auth) {
        
        Long requestUserId = (Long) auth.getPrincipal();
        if (!userid.equals(requestUserId)) {
            throw new RuntimeException("본인만 로그아웃할 수 있습니다");
        }
        
        authService.logout(userid);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @PathVariable Long userid,
            Authentication auth) {
        
        Long requestUserId = (Long) auth.getPrincipal();
        userService.deleteUser(userid, requestUserId);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 성공", null));
    }

    @Operation(summary = "회원 QR 코드 조회")
    @GetMapping("/qr")
    public ResponseEntity<ApiResponse<QrCodeService.QrCodeResponse>> getUserQr(
            @PathVariable Long userid,
            Authentication auth) {
        
        Long requestUserId = (Long) auth.getPrincipal();
        if (!userid.equals(requestUserId)) {
            throw new RuntimeException("본인의 QR 코드만 조회할 수 있습니다");
        }
        
        QrCodeService.QrCodeResponse qrCode = qrCodeService.generateUserQrCode(userid);
        return ResponseEntity.ok(ApiResponse.success("QR 코드 생성 성공", qrCode));
    }
}
