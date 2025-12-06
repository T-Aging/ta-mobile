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
@RequestMapping("/t-age/users/{userid}/custom-menus")
@RequiredArgsConstructor
@Tag(name = "커스텀 메뉴", description = "나만의 메뉴 관리 API")
public class CustomController {

    // CustomMenuService 혹은 CustomService (프로젝트 내 클래스명 확인 후 사용)
    private final CustomService customService; 

    @Operation(summary = "커스텀 메뉴 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getCustomMenus(@PathVariable Integer userid) {
        var menus = customService.getAllCustomMenus(userid);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 조회 성공", menus));
    }

    @Operation(summary = "커스텀 메뉴 상세 조회")
    @GetMapping("/{customId}")
    public ResponseEntity<ApiResponse<?>> getCustomMenuDetail(
            @PathVariable Integer userid,
            @PathVariable Integer customId) {
        var menu = customService.getCustomMenuDetail(userid, customId);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 상세 조회 성공", menu));
    }

    @Operation(summary = "커스텀 메뉴 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> createCustomMenu(
            @PathVariable Integer userid,
            @Valid @RequestBody CustomMenuDto.CreateRequest request) {
        CustomMenuDto.Response response = customService.createCustomMenu(userid, request);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 생성 성공", response));
    }

    @Operation(summary = "커스텀 메뉴 수정")
    @PutMapping("/{customId}")
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> updateCustomMenu(
            @PathVariable Integer userid,
            @PathVariable Integer customId,
            @Valid @RequestBody CustomMenuDto.UpdateRequest request) {
        request.setCustomId(customId); // PathVariable ID 주입
        CustomMenuDto.Response response = customService.updateCustomMenu(userid, request);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 수정 성공", response));
    }

    @Operation(summary = "커스텀 메뉴 삭제")
    @DeleteMapping("/{customId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomMenu(
            @PathVariable Integer userid,
            @PathVariable Integer customId) {
        customService.deleteCustomMenu(userid, customId);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 삭제 성공", null));
    }
}