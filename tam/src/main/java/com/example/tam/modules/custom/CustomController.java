package com.example.tam.modules.custom;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.CustomMenuDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/users/{userid}/custom")
@RequiredArgsConstructor
@Tag(name = "커스텀 메뉴", description = "커스텀 메뉴 관리 API")
public class CustomController {

    private final CustomService customService;

    @Operation(summary = "커스텀 주문 조회 / 최근 주문 조회")
    @GetMapping("/gen")
    public ResponseEntity<ApiResponse<?>> getCustomMenus(@PathVariable Integer userid) {
        var menus = customService.getAllCustomMenus(userid);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 조회 성공", menus));
    }

    @Operation(summary = "커스텀 주문 생성 / 최근 주문 생성")
    @PostMapping("/gen")
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> createCustomMenu(
            @PathVariable Integer userid,
            @Valid @RequestBody CustomMenuDto.CreateRequest request) {
        
        CustomMenuDto.Response response = customService.createCustomMenu(userid, request);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 생성 성공", response));
    }

    @Operation(summary = "커스텀 메뉴 수정")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> updateCustomMenu(
            @PathVariable Integer userid,
            @Valid @RequestBody CustomMenuDto.UpdateRequest request) {
        
        CustomMenuDto.Response response = customService.updateCustomMenu(userid, request);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 수정 성공", response));
    }

    @Operation(summary = "커스텀 메뉴 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteCustomMenu(
            @PathVariable Integer userid,
            @RequestParam Integer customId) {
        
        customService.deleteCustomMenu(userid, customId);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 삭제 성공", null));
    }

    @Operation(summary = "커스텀 메뉴 조회 / 커스텀 상세 보기(최근 생성 순)")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchCustomMenu(
            @PathVariable Integer userid,
            @RequestParam(required = false) Integer customId) {
        
        if (customId != null) {
            var menu = customService.getCustomMenuDetail(userid, customId);
            return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 상세 조회 성공", menu));
        }
        
        var menus = customService.getAllCustomMenus(userid);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 목록 조회 성공", menus));
    }
}
