package com.example.tam.modules.custom;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.CustomMenuDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/users/{userid}/custom")
@RequiredArgsConstructor
@Tag(name = "커스텀 메뉴", description = "커스텀 메뉴 관리 API")
public class CustomMenuController {

    private final CustomMenuService customMenuService;

    @Operation(summary = "커스텀 메뉴 조회")
    @GetMapping("/gen")
    public ResponseEntity<ApiResponse<?>> getCustomMenus(
            @PathVariable Long userid,
            @RequestParam(required = false) String type,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        if ("recent".equals(type)) {
            var menus = customMenuService.getRecentMenus(userid);
            return ResponseEntity.ok(ApiResponse.success("최근 주문 조회 성공", menus));
        } else if ("favorite".equals(type)) {
            var menus = customMenuService.getFavoriteMenus(userid);
            return ResponseEntity.ok(ApiResponse.success("즐겨찾기 메뉴 조회 성공", menus));
        }
        
        var menus = customMenuService.getAllCustomMenus(userid);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 조회 성공", menus));
    }

    @Operation(summary = "커스텀 메뉴 생성")
    @PostMapping("/gen")
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> createCustomMenu(
            @PathVariable Long userid,
            @Valid @RequestBody CustomMenuDto.CreateRequest request,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        CustomMenuDto.Response response = customMenuService.createCustomMenu(userid, request);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 생성 성공", response));
    }

    @Operation(summary = "커스텀 메뉴 수정")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> updateCustomMenu(
            @PathVariable Long userid,
            @Valid @RequestBody CustomMenuDto.UpdateRequest request,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        CustomMenuDto.Response response = customMenuService.updateCustomMenu(userid, request);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 수정 성공", response));
    }

    @Operation(summary = "커스텀 메뉴 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteCustomMenu(
            @PathVariable Long userid,
            @RequestParam Long menuId,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        customMenuService.deleteCustomMenu(userid, menuId);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 삭제 성공", null));
    }

    @Operation(summary = "커스텀 메뉴 검색 및 조회 (최근 생성 순)")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchCustomMenu(
            @PathVariable Long userid,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        var menus = customMenuService.getAllCustomMenus(userid);
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 검색 성공", menus));
    }

    private void validateUser(Long userid, Authentication auth) {
        Long requestUserId = (Long) auth.getPrincipal();
        if (!userid.equals(requestUserId)) {
            throw new RuntimeException("본인의 메뉴만 조회할 수 있습니다");
        }
    }
}
