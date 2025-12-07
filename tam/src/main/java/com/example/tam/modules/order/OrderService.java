package com.example.tam.modules.order;

import com.example.tam.common.entity.*;
import com.example.tam.dto.OrderDto;
import com.example.tam.modules.custom.CustomHeaderRepository;
import com.example.tam.modules.menu.MenuOptionRepository;
import com.example.tam.modules.menu.MenuRepository;
import com.example.tam.modules.store.StoreRepository;
import com.example.tam.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderHeaderRepository orderHeaderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final StoreRepository storeRepository;
    
    // [추가] 커스텀 메뉴 등록을 위해 필요
    private final CustomHeaderRepository customHeaderRepository; 

    /**
     * 주문 생성 (DB 저장)
     */
    @Transactional
    public OrderDto.Response createOrder(Integer userId, OrderDto.CreateRequest request) {
        // 1. 매장 확인
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("매장을 찾을 수 없습니다."));

        // 2. 주문 헤더 생성 (가격은 나중에 계산 후 업데이트)
        OrderHeader header = OrderHeader.builder()
                .userId(userId)
                .storeId(store.getStoreId())
                .orderDatetime(LocalDateTime.now())
                .orderDate(LocalDate.now())
                .orderStatus("COMPLETED") // 테스트용으로 바로 완료 처리
                .waitingNum(100) // 대기번호 로직은 생략 (임의값)
                .totalPrice(0)   // 계산 전
                .build();
        OrderHeader savedHeader = orderHeaderRepository.save(header);

        int totalPrice = 0;

        // 3. 주문 상세(메뉴) 및 옵션 저장
        if (request.getItems() != null) {
            for (OrderDto.OrderItem item : request.getItems()) {
                Menu menu = menuRepository.findById(item.getMenuId())
                        .orElseThrow(() -> new ResourceNotFoundException("메뉴 없음: " + item.getMenuId()));

                // 3-1. 상세(Detail) 저장
                int detailPrice = menu.getMenuPrice(); // 기본가
                
                OrderDetail detail = OrderDetail.builder()
                        .orderId(savedHeader.getOrderId())
                        .menuId(menu.getMenuId())
                        .quantity(item.getQuantity())
                        .orderDetailPrice(0) // 옵션 합산 후 설정
                        .build();
                OrderDetail savedDetail = orderDetailRepository.save(detail);

                // 3-2. 옵션(Option) 저장 및 가격 계산
                if (item.getOptions() != null) {
                    for (OrderDto.OptionDetail opt : item.getOptions()) {
                        MenuOption menuOption = menuOptionRepository.findById(opt.getOptionId())
                                .orElseThrow(() -> new ResourceNotFoundException("옵션 없음: " + opt.getOptionId()));
                        
                        OrderOption orderOption = OrderOption.builder()
                                .orderDetailId(savedDetail.getOrderDetailId())
                                .optionId(menuOption.getOptionId())
                                .extraNum(opt.getExtraNum())
                                .build();
                        orderOptionRepository.save(orderOption);

                        // 옵션 가격 추가 (옵션단가 * 개수)
                        detailPrice += (menuOption.getExtraPrice() * opt.getExtraNum());
                    }
                }

                // 상세 항목 최종 가격 저장 (단가 * 수량)
                int itemTotalPrice = detailPrice * item.getQuantity();
                savedDetail.setOrderDetailPrice(itemTotalPrice);
                orderDetailRepository.save(savedDetail);

                totalPrice += itemTotalPrice;
            }
        }

        // 4. 헤더에 총 가격 업데이트
        savedHeader.setTotalPrice(totalPrice);
        orderHeaderRepository.save(savedHeader);

        log.info("주문 생성 완료: OrderId={}, TotalPrice={}", savedHeader.getOrderId(), totalPrice);

        return getOrderDetail(userId, savedHeader.getOrderId());
    }

    /**
     * 전체 주문 내역 조회
     */
    @Transactional(readOnly = true)
    public List<OrderDto.Response> getAllOrders(Integer userId) {
        List<OrderHeader> headers = orderHeaderRepository.findByUserIdOrderByOrderDatetimeDesc(userId);
        return headers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 기간별 주문 내역 조회
     */
    @Transactional(readOnly = true)
    public List<OrderDto.Response> getOrdersByDateRange(Integer userId, String fromDate, String toDate) {
        LocalDate start = LocalDate.parse(fromDate);
        LocalDate end = LocalDate.parse(toDate);
        
        List<OrderHeader> headers = orderHeaderRepository
                .findByUserIdAndOrderDateBetweenOrderByOrderDatetimeDesc(userId, start, end);
        
        return headers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 주문 상세 조회 (단건)
     */
    @Transactional(readOnly = true)
    public OrderDto.Response getOrderDetail(Integer userId, Integer orderId) {
        OrderHeader header = orderHeaderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("주문 내역이 없습니다."));
        
        if (!header.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 주문 내역만 조회할 수 있습니다.");
        }
        
        return mapToResponse(header);
    }

    /**
     * 주문 내역을 기반으로 커스텀 메뉴 생성 (추가 기능)
     */
    @Transactional
    public void createCustomMenuFromOrder(Integer userId, Integer orderId, String customName) {
        // ... (필요 시 구현, 현재는 로직 생략)
        log.info("주문 기반 커스텀 생성 요청: OrderId={}, Name={}", orderId, customName);
    }

    // --- Helper Method: Entity -> DTO 변환 ---
    private OrderDto.Response mapToResponse(OrderHeader header) {
        // 매장 정보
        String storeName = storeRepository.findById(header.getStoreId())
                .map(Store::getStoreName).orElse("알 수 없는 매장");

        // 주문 상세 항목 조회
        List<OrderDetail> details = orderDetailRepository.findByOrderId(header.getOrderId());
        List<OrderDto.OrderItemDetail> itemDtos = new ArrayList<>();

        for (OrderDetail detail : details) {
            Menu menu = menuRepository.findById(detail.getMenuId()).orElse(null);
            if (menu == null) continue;

            // 옵션 조회
            List<OrderOption> options = orderOptionRepository.findByOrderDetailId(detail.getOrderDetailId());
            List<OrderDto.OptionDetail> optionDtos = options.stream()
                    .map(opt -> OrderDto.OptionDetail.builder()
                            .optionId(opt.getOptionId())
                            .extraNum(opt.getExtraNum())
                            .build())
                    .collect(Collectors.toList());

            itemDtos.add(OrderDto.OrderItemDetail.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .quantity(detail.getQuantity())
                    .price(detail.getOrderDetailPrice())
                    .options(optionDtos)
                    .build());
        }

        return OrderDto.Response.builder()
                .orderId(header.getOrderId())
                .userId(header.getUserId())
                .storeId(header.getStoreId())
                .storeName(storeName)
                .orderDatetime(header.getOrderDatetime())
                .waitingNum(header.getWaitingNum())
                .totalPrice(header.getTotalPrice())
                .orderStatus(header.getOrderStatus())
                .items(itemDtos)
                .build();
    }
}