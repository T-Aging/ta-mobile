package com.example.tam.modules.push;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.PushDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/t-age/users/{userid}")
@RequiredArgsConstructor
@Tag(name = "푸시 알림", description = "푸시 알림 조회 API")
public class PushController {

    private final PushService pushService;

    @Operation(summary = "키오스크 주문 확인 알림 / 카카오톡 알림 / 모바일 영수증 도착 알림")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PushDto.Response>>> getPushHistory(
            @PathVariable Integer userid,
            @RequestParam(required = false) String type) {
        
        List<PushDto.Response> response = type != null 
            ? pushService.getPushHistoryByType(userid, type)
            : pushService.getAllPushHistory(userid);
        
        return ResponseEntity.ok(ApiResponse.success("푸시 알림 조회 성공", response));
    }
}
