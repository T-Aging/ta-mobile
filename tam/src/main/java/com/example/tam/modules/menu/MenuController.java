package com.example.tam.modules.menu;

import com.example.tam.common.entity.Menu;
// import com.example.tam.common.entity.MenuOption; // 삭제된 엔티티 주석
import com.example.tam.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections; // 추가

@RestController
@RequestMapping("/t-age/menu")
@RequiredArgsConstructor
@Tag(name = "메뉴", description = "메뉴 조회 API")
public class MenuController {

    private final MenuRepository menuRepository;
    // private final MenuOptionRepository menuOptionRepository; // 주석

    @Operation(summary = "전체 메뉴 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Menu>>> getAllMenus() {
        List<Menu> menus = menuRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("메뉴 조회 성공", menus));
    }

    @Operation(summary = "메뉴 상세 조회")
    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<Menu>> getMenuDetail(@PathVariable Integer menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));
        return ResponseEntity.ok(ApiResponse.success("메뉴 상세 조회 성공", menu));
    }

    @Operation(summary = "메뉴 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Menu>>> searchMenus(@RequestParam String keyword) {
        // [수정] findByMenuNameContaining -> findByNameContaining (Repository 변경 사항 반영)
        List<Menu> menus = menuRepository.findByNameContaining(keyword);
        return ResponseEntity.ok(ApiResponse.success("메뉴 검색 성공", menus));
    }

    @Operation(summary = "메뉴 옵션 조회")
    @GetMapping("/{menuId}/options")
    public ResponseEntity<ApiResponse<List<?>>> getMenuOptions(@PathVariable Integer menuId) {
        // TODO: 특정 메뉴의 옵션만 조회하는 로직 필요 (MenuOptionMapping 활용)
        // List<MenuOption> options = menuOptionRepository.findAll();
        // return ResponseEntity.ok(ApiResponse.success("메뉴 옵션 조회 성공", options));
        return ResponseEntity.ok(ApiResponse.success("메뉴 옵션 조회 성공 (기능 비활성화됨)", Collections.emptyList()));
    }
}