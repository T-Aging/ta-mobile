package com.example.tam.modules.push;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.PushDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections; // 추가

@RestController
@RequestMapping("/t-age/users/{userid}/notifications")
@RequiredArgsConstructor
@Tag(name = "푸시 알림", description = "알림 내역 조회 API")
public class PushController {

    private final PushService pushService;

    @Operation(summary = "알림 내역 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PushDto.Response>>> getPushHistory(
            @PathVariable Integer userid,
            @Parameter(description = "알림 타입") @RequestParam(required = false) String type) {
        
        // List<PushDto.Response> response;
        // if (type != null && !type.isEmpty()) {
        //     response = pushService.getPushHistoryByType(userid, type);
        // } else {
        //     response = pushService.getAllPushHistory(userid);
        // }
        
        // return ResponseEntity.ok(ApiResponse.success("알림 조회 성공", response));
        return ResponseEntity.ok(ApiResponse.success("알림 조회 성공 (기능 비활성화됨)", Collections.emptyList()));
    }
}