package com.example.tam.modules.custom;

import com.example.tam.common.entity.*;
import com.example.tam.dto.CustomMenuDto;
import com.example.tam.modules.menu.MenuRepository;
import com.example.tam.modules.menu.MenuOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomService {
    
    private final CustomHeaderRepository customHeaderRepository;
    private final CustomDetailRepository customDetailRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;

    @Transactional(readOnly = true)
    public List<CustomMenuDto.Response> getAllCustomMenus(Integer userId) {
        List<CustomHeader> headers = customHeaderRepository.findByUserIdOrderByCustomIdDesc(userId);
        
        return headers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomMenuDto.Response getCustomMenuDetail(Integer userId, Integer customId) {
        CustomHeader header = customHeaderRepository.findById(customId)
                .orElseThrow(() -> new RuntimeException("커스텀 메뉴를 찾을 수 없습니다"));
        
        if (!header.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 커스텀 메뉴만 조회할 수 있습니다");
        }
        
        return convertToResponse(header);
    }

    @Transactional
    public CustomMenuDto.Response createCustomMenu(Integer userId, CustomMenuDto.CreateRequest request) {
        // 메뉴 존재 확인
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));

        // CustomHeader 생성
        CustomHeader header = CustomHeader.builder()
                .userId(userId)
                .menuId(request.getMenuId())
                .customName(request.getCustomName())
                .build();
        
        CustomHeader savedHeader = customHeaderRepository.save(header);

        // CustomDetail 생성
        if (request.getOptions() != null) {
            for (CustomMenuDto.OptionDetail option : request.getOptions()) {
                CustomDetail detail = CustomDetail.builder()
                        .customId(savedHeader.getCustomId())
                        .optionId(option.getOptionId())
                        .extraNum(option.getExtraNum())
                        .build();
                customDetailRepository.save(detail);
            }
        }

        log.info("커스텀 메뉴 생성 - userId: {}, customId: {}", userId, savedHeader.getCustomId());
        return convertToResponse(savedHeader);
    }

    @Transactional
    public CustomMenuDto.Response updateCustomMenu(Integer userId, CustomMenuDto.UpdateRequest request) {
        CustomHeader header = customHeaderRepository.findById(request.getCustomId())
                .orElseThrow(() -> new RuntimeException("커스텀 메뉴를 찾을 수 없습니다"));
        
        if (!header.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 커스텀 메뉴만 수정할 수 있습니다");
        }

        // 이름 수정
        if (request.getCustomName() != null) {
            header.setCustomName(request.getCustomName());
            customHeaderRepository.save(header);
        }

        // 기존 옵션 삭제 후 새로 추가
        if (request.getOptions() != null) {
            List<CustomDetail> existingDetails = customDetailRepository.findByCustomId(header.getCustomId());
            customDetailRepository.deleteAll(existingDetails);

            for (CustomMenuDto.OptionDetail option : request.getOptions()) {
                CustomDetail detail = CustomDetail.builder()
                        .customId(header.getCustomId())
                        .optionId(option.getOptionId())
                        .extraNum(option.getExtraNum())
                        .build();
                customDetailRepository.save(detail);
            }
        }

        log.info("커스텀 메뉴 수정 - userId: {}, customId: {}", userId, header.getCustomId());
        return convertToResponse(header);
    }

    @Transactional
    public void deleteCustomMenu(Integer userId, Integer customId) {
        CustomHeader header = customHeaderRepository.findById(customId)
                .orElseThrow(() -> new RuntimeException("커스텀 메뉴를 찾을 수 없습니다"));
        
        if (!header.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 커스텀 메뉴만 삭제할 수 있습니다");
        }

        // 상세 먼저 삭제
        List<CustomDetail> details = customDetailRepository.findByCustomId(customId);
        customDetailRepository.deleteAll(details);

        // 헤더 삭제
        customHeaderRepository.delete(header);
        
        log.info("커스텀 메뉴 삭제 - userId: {}, customId: {}", userId, customId);
    }

    private CustomMenuDto.Response convertToResponse(CustomHeader header) {
        Menu menu = menuRepository.findById(header.getMenuId()).orElse(null);
        List<CustomDetail> details = customDetailRepository.findByCustomId(header.getCustomId());

        // 총 가격 계산
        int totalPrice = menu != null ? menu.getMenuPrice() : 0;
        List<CustomMenuDto.OptionDetail> options = details.stream()
                .map(detail -> {
                    MenuOption option = menuOptionRepository.findById(detail.getOptionId()).orElse(null);
                    if (option != null && option.getExtraPrice() != null) {
                        totalPrice += option.getExtraPrice() * (detail.getExtraNum() != null ? detail.getExtraNum() : 1);
                    }
                    return CustomMenuDto.OptionDetail.builder()
                            .optionId(detail.getOptionId())
                            .extraNum(detail.getExtraNum())
                            .build();
                })
                .collect(Collectors.toList());

        return CustomMenuDto.Response.builder()
                .customId(header.getCustomId())
                .userId(header.getUserId())
                .menuId(header.getMenuId())
                .menuName(menu != null ? menu.getMenuName() : null)
                .customName(header.getCustomName())
                .totalPrice(totalPrice)
                .options(options)
                .build();
    }
}
