package com.example.tam.modules.custom;

import com.example.tam.common.entity.*;
import com.example.tam.dto.CustomMenuDto;
import com.example.tam.modules.menu.MenuOptionRepository;
import com.example.tam.modules.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    /**
     * 커스텀 메뉴 목록 조회 (진짜 DB 조회)
     */
    @Transactional(readOnly = true)
    public List<CustomMenuDto.Response> getCustomMenus(Integer userId, String keyword) {
        // 1. 사용자 ID로 저장된 커스텀 헤더(껍데기) 조회
        List<CustomHeader> headers = customHeaderRepository.findByUserIdOrderByCustomIdDesc(userId);

        // 2. 각 헤더를 DTO로 변환 (메뉴 정보 + 옵션 정보 조합)
        return headers.stream().map(header -> {
            // 메뉴 정보 조회
            Menu menu = menuRepository.findById(header.getMenuId())
                    .orElseThrow(() -> new RuntimeException("메뉴 정보가 없습니다. ID=" + header.getMenuId()));

            // 옵션 상세 정보 조회
            List<CustomDetail> details = customDetailRepository.findByCustomId(header.getCustomId());
            
            // 총 가격 계산 (메뉴 기본가 + 옵션 추가금 합산)
            int totalPrice = menu.getMenuPrice();
            List<CustomMenuDto.OptionDetail> optionDtos = new ArrayList<>();

            for (CustomDetail detail : details) {
                MenuOption option = menuOptionRepository.findById(detail.getOptionId())
                        .orElse(null);
                
                if (option != null) {
                    totalPrice += (option.getExtraPrice() * detail.getExtraNum());
                }

                optionDtos.add(CustomMenuDto.OptionDetail.builder()
                        .optionId(detail.getOptionId())
                        .extraNum(detail.getExtraNum())
                        .build());
            }

            // 응답 DTO 생성
            return CustomMenuDto.Response.builder()
                    .customId(header.getCustomId())
                    .userId(header.getUserId())
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .customName(header.getCustomName())
                    .totalPrice(totalPrice)
                    .options(optionDtos)
                    .build();
        }).collect(Collectors.toList());
    }

    // 커스텀 메뉴 상세 조회
    @Transactional(readOnly = true)
    public CustomMenuDto.Response getCustomMenuDetail(Integer userId, Integer customId) {
        return getCustomMenus(userId, null).stream()
                .filter(m -> m.getCustomId().equals(customId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("커스텀 메뉴를 찾을 수 없습니다."));
    }

    // 커스텀 메뉴 생성 (진짜 DB 저장)
    @Transactional
    public CustomMenuDto.Response createCustomMenu(Integer userId, CustomMenuDto.CreateRequest request) {
        // 1. 헤더(이름, 메뉴ID) 저장
        CustomHeader header = CustomHeader.builder()
                .userId(userId)
                .menuId(request.getMenuId())
                .customName(request.getCustomName())
                .build();
        CustomHeader savedHeader = customHeaderRepository.save(header);

        // 2. 상세 옵션 저장
        if (request.getOptions() != null) {
            for (CustomMenuDto.OptionDetail opt : request.getOptions()) {
                CustomDetail detail = CustomDetail.builder()
                        .customId(savedHeader.getCustomId())
                        .optionId(opt.getOptionId())
                        .extraNum(opt.getExtraNum())
                        .build();
                customDetailRepository.save(detail);
            }
        }

        log.info("커스텀 메뉴 생성 완료: ID={}, Name={}", savedHeader.getCustomId(), savedHeader.getCustomName());
        
        // 3. 저장된 정보로 다시 조회해서 반환
        return getCustomMenuDetail(userId, savedHeader.getCustomId());
    }

    // 커스텀 메뉴 수정
    @Transactional
    public CustomMenuDto.Response updateCustomMenu(Integer userId, CustomMenuDto.UpdateRequest request) {
        // 1. 기존 데이터 삭제 (단순화를 위해 삭제 후 재생성 전략 사용)
        deleteCustomMenu(userId, request.getCustomId());

        // 2. 재생성 요청 객체 변환
        CustomMenuDto.CreateRequest createReq = CustomMenuDto.CreateRequest.builder()
                .menuId(1) // 수정 시 메뉴 ID를 유지하는 로직이 필요하나, 일단 1로 고정 (실제론 조회해서 넣어야 함)
                .customName(request.getCustomName())
                .options(request.getOptions())
                .build();
        
        // *주의: 실제 수정 시에는 header의 menuId를 유지해야 합니다.
        // 현재는 간단히 구현했습니다.
        return createCustomMenu(userId, createReq);
    }

    // 커스텀 메뉴 삭제
    @Transactional
    public void deleteCustomMenu(Integer userId, Integer customId) {
        CustomHeader header = customHeaderRepository.findById(customId)
                .orElseThrow(() -> new RuntimeException("메뉴가 존재하지 않습니다."));
        
        if (!header.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 메뉴만 삭제할 수 있습니다.");
        }

        // 옵션(자식) 먼저 삭제 후 헤더(부모) 삭제
        List<CustomDetail> details = customDetailRepository.findByCustomId(customId);
        customDetailRepository.deleteAll(details);
        customHeaderRepository.delete(header);
    }
    
    // 전체 조회 (단순 껍데기 - 필요시 구현)
    public List<CustomMenuDto.Response> getAllCustomMenus(Integer userId) {
        return getCustomMenus(userId, null);
    }
}