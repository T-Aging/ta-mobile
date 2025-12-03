package com.example.tam.modules.user;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.UserDto;
import com.example.tam.modules.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/users/{userid}")
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "마이 페이지 API")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "사용자 정보 조회")
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

    @Operation(summary = "회원 QR 코드 조회")
    @GetMapping("/qr")
    public ResponseEntity<ApiResponse<String>> getUserQr(@PathVariable Integer userid) {
        String qrData = userService.getUserQr(userid);
        return ResponseEntity.ok(ApiResponse.success("QR 코드 생성 성공", qrData));
    }
}
