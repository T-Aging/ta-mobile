package com.example.tam.modules.custom;

import com.example.tam.dto.CustomMenuDto;
import com.example.tam.modules.custom.entity.CustomMenu;
import com.example.tam.modules.user.UserRepository;
import com.example.tam.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomMenuService {
    
    private final CustomMenuRepository customMenuRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CustomMenuDto.Response> getAllCustomMenus(Long userId) {
        return customMenuRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomMenuDto.Response> getRecentMenus(Long userId) {
        return customMenuRepository.findByUserIdAndIsRecentTrue(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomMenuDto.Response> getFavoriteMenus(Long userId) {
        return customMenuRepository.findByUserIdAndIsFavoriteTrueOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomMenuDto.Response createCustomMenu(Long userId, CustomMenuDto.CreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        CustomMenu menu = CustomMenu.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .options(request.getOptionsJson())
                .totalPrice(request.getTotalPrice())
                .isFavorite(false)
                .isRecent(true)
                .build();

        CustomMenu savedMenu = customMenuRepository.save(menu);
        log.info("커스텀 메뉴 생성 - userId: {}, menuId: {}", userId, savedMenu.getId());
        
        return mapToResponse(savedMenu);
    }

    @Transactional
    public CustomMenuDto.Response updateCustomMenu(Long userId, CustomMenuDto.UpdateRequest request) {
        CustomMenu menu = customMenuRepository.findByIdAndUserId(request.getId(), userId)
                .orElseThrow(() -> new RuntimeException("커스텀 메뉴를 찾을 수 없습니다"));

        if (request.getName() != null) menu.setName(request.getName());
        if (request.getDescription() != null) menu.setDescription(request.getDescription());
        if (request.getOptionsJson() != null) menu.setOptions(request.getOptionsJson());
        if (request.getTotalPrice() != null) menu.setTotalPrice(request.getTotalPrice());
        if (request.getIsFavorite() != null) menu.setIsFavorite(request.getIsFavorite());

        CustomMenu updatedMenu = customMenuRepository.save(menu);
        log.info("커스텀 메뉴 수정 - userId: {}, menuId: {}", userId, updatedMenu.getId());
        
        return mapToResponse(updatedMenu);
    }

    @Transactional
    public void deleteCustomMenu(Long userId, Long menuId) {
        customMenuRepository.findByIdAndUserId(menuId, userId)
                .orElseThrow(() -> new RuntimeException("커스텀 메뉴를 찾을 수 없습니다"));
        
        customMenuRepository.deleteByIdAndUserId(menuId, userId);
        log.info("커스텀 메뉴 삭제 - userId: {}, menuId: {}", userId, menuId);
    }

    private CustomMenuDto.Response mapToResponse(CustomMenu menu) {
        return CustomMenuDto.Response.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .optionsJson(menu.getOptions())
                .totalPrice(menu.getTotalPrice())
                .isFavorite(menu.getIsFavorite())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}
