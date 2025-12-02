package com.example.tam.modules.custom;

import com.example.tam.modules.custom.entity.CustomMenu;
import com.example.tam.modules.user.UserRepository; // Repository 필요
import com.example.tam.modules.user.entity.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/t-age/users/{userid}/custom")
@RequiredArgsConstructor
public class CustomMenuController {

    private final CustomMenuRepository customMenuRepository;
    private final UserRepository userRepository;

    // 커스텀 주문 조회 / 최근 주문 조회 (요청 파라미터로 구분 예시)
    @GetMapping("/gen")
    public ResponseEntity<?> getCustomOrRecent(@PathVariable Long userid, @RequestParam(required = false) String type) {
        if ("recent".equals(type)) {
            return ResponseEntity.ok(customMenuRepository.findByUserIdAndIsRecentTrue(userid));
        }
        return ResponseEntity.ok(customMenuRepository.findByUserId(userid));
    }

    // 커스텀 주문 생성 / 최근 주문 생성
    @PostMapping("/gen")
    public ResponseEntity<?> createCustomMenu(@PathVariable Long userid, @RequestBody CustomMenu menuDto) {
        User user = userRepository.findById(userid).orElseThrow();
        
        CustomMenu menu = CustomMenu.builder()
                .user(user)
                .name(menuDto.getName())
                .options(menuDto.getOptions())
                .isRecent(true) // 생성 시 최근 항목으로 설정
                .build();
        
        customMenuRepository.save(menu);
        return ResponseEntity.ok("커스텀 메뉴 생성 완료");
    }

    // 커스텀 메뉴 수정
    @PutMapping("/update")
    public ResponseEntity<?> updateCustomMenu(@PathVariable Long userid, @RequestBody CustomMenu menuDto) {
        CustomMenu menu = customMenuRepository.findById(menuDto.getId()).orElseThrow();
        menu.setOptions(menuDto.getOptions());
        menu.setName(menuDto.getName());
        customMenuRepository.save(menu);
        return ResponseEntity.ok("수정 완료");
    }

    // 커스텀 메뉴 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCustomMenu(@PathVariable Long userid, @RequestParam Long menuId) {
        customMenuRepository.deleteById(menuId);
        return ResponseEntity.ok("삭제 완료");
    }

    // 커스텀 메뉴 검색 및 조회
    @GetMapping("/search")
    public ResponseEntity<?> searchCustomMenu(@PathVariable Long userid) {
        // 최근 생성 순 정렬 조회 로직 필요
        List<CustomMenu> menus = customMenuRepository.findByUserIdOrderByCreatedAtDesc(userid);
        return ResponseEntity.ok(menus);
    }
}
