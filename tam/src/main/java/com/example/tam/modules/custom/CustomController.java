package com.example.tam.modules.custom;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.CustomMenuDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections; // 추가

@RestController
@RequestMapping("/t-age/users/{userid}/custom-menus")
@RequiredArgsConstructor
@Tag(name = "커스텀 메뉴", description = "나만의 메뉴 관리 API")
public class CustomController {

    private final CustomService customService;

    @Operation(summary = "커스텀 메뉴 목록 조회 (검색 및 정렬 포함)", 
               description = "keyword가 있으면 검색, 없으면 전체 조회. 최근 생성 순으로 반환됩니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomMenuDto.Response>>> getCustomMenus(
            @PathVariable Integer userid,
            @Parameter(description = "검색어") @RequestParam(required = false) String keyword) {
        
        // Service 내부에서 keyword 유무에 따라 검색 로직 분기 처리 권장
        // List<CustomMenuDto.Response> menus = customService.getCustomMenus(userid, keyword);
        // return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 조회 성공", menus));
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 조회 성공 (기능 비활성화됨)", Collections.emptyList()));
    }

    @Operation(summary = "커스텀 메뉴 상세 조회")
    @GetMapping("/{customId}")
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> getCustomMenuDetail(
            @PathVariable Integer userid,
            @PathVariable Integer customId) {
        // CustomMenuDto.Response menu = customService.getCustomMenuDetail(userid, customId);
        // return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 상세 조회 성공", menu));
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 상세 조회 성공 (기능 비활성화됨)", null));
    }

    @Operation(summary = "커스텀 메뉴 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> createCustomMenu(
            @PathVariable Integer userid,
            @Valid @RequestBody CustomMenuDto.CreateRequest request) {
        // CustomMenuDto.Response response = customService.createCustomMenu(userid, request);
        // return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 생성 성공", response));
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 생성 성공 (기능 비활성화됨)", null));
    }

    @Operation(summary = "커스텀 메뉴 수정")
    @PutMapping("/{customId}")
    public ResponseEntity<ApiResponse<CustomMenuDto.Response>> updateCustomMenu(
            @PathVariable Integer userid,
            @PathVariable Integer customId,
            @Valid @RequestBody CustomMenuDto.UpdateRequest request) {
        // Path ID와 Body 데이터 일치 보장
        // request.setCustomId(customId); 
        // CustomMenuDto.Response response = customService.updateCustomMenu(userid, request);
        // return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 수정 성공", response));
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 수정 성공 (기능 비활성화됨)", null));
    }

    @Operation(summary = "커스텀 메뉴 삭제")
    @DeleteMapping("/{customId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomMenu(
            @PathVariable Integer userid,
            @PathVariable Integer customId) {
        // customService.deleteCustomMenu(userid, customId);
        // return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 삭제 성공", null));
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 삭제 성공 (기능 비활성화됨)", null));
    }
}