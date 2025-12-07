package com.example.tam.modules.order;

import com.example.tam.common.entity.*;
import com.example.tam.dto.OrderDto;
import com.example.tam.modules.menu.MenuRepository;
import com.example.tam.modules.store.StoreRepository;
import com.example.tam.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final StoreRepository storeRepository;

    @Transactional
    public OrderDto.Response createOrder(Integer userId, OrderDto.CreateRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("매장을 찾을 수 없습니다."));

        // [수정] @Builder 없음 -> new 객체 생성 및 Setter 사용
        OrderHeader header = new OrderHeader();
        header.setUserId(userId);
        header.setStore(store);
        header.setOrderDateTime(LocalDateTime.now());
        header.setOrderDate(LocalDate.now());
        
        // [수정] COMPLETED 상태가 없음 -> PAID(결제완료)로 변경
        header.setOrderState(OrderHeader.OrderState.PAID); 
        
        header.setWaitingNum(100);
        header.setTotalPrice(BigDecimal.ZERO);
        
        OrderHeader savedHeader = orderHeaderRepository.save(header);

        BigDecimal totalPrice = BigDecimal.ZERO;

        if (request.getItems() != null) {
            for (OrderDto.OrderItem item : request.getItems()) {
                Menu menu = menuRepository.findById(item.getMenuId())
                        .orElseThrow(() -> new ResourceNotFoundException("메뉴 없음: " + item.getMenuId()));

                // [수정] getMenuPrice() -> getPrice()
                BigDecimal detailPrice = menu.getPrice(); 
                
                // [수정] @Builder 없음 -> new 객체 생성
                OrderDetail detail = new OrderDetail();
                detail.setOrderHeader(savedHeader);
                detail.setMenu(menu);
                detail.setQuantity(item.getQuantity());
                detail.setOrderDetailPrice(BigDecimal.ZERO);

                OrderDetail savedDetail = orderDetailRepository.save(detail);

                // 옵션 관련 로직은 엔티티 부재로 주석 처리됨
                if (item.getOptions() != null) {
                    /*
                    for (OrderDto.OptionDetail opt : item.getOptions()) {
                         // 옵션 처리 로직...
                    }
                    */
                }

                // 가격 계산 (BigDecimal 연산)
                BigDecimal itemQuantity = new BigDecimal(item.getQuantity());
                BigDecimal itemTotalPrice = detailPrice.multiply(itemQuantity);
                
                savedDetail.setOrderDetailPrice(itemTotalPrice);
                orderDetailRepository.save(savedDetail);

                totalPrice = totalPrice.add(itemTotalPrice);
            }
        }

        savedHeader.setTotalPrice(totalPrice);
        orderHeaderRepository.save(savedHeader);

        // [수정] getOrderId() -> getId()
        log.info("주문 생성 완료: OrderId={}, TotalPrice={}", savedHeader.getId(), totalPrice);

        return getOrderDetail(userId, savedHeader.getId());
    }

    @Transactional(readOnly = true)
    public List<OrderDto.Response> getAllOrders(Integer userId) {
        // [수정] OrderDatetime -> OrderDateTime (메서드명 변경 반영)
        List<OrderHeader> headers = orderHeaderRepository.findByUserIdOrderByOrderDateTimeDesc(userId);
        return headers.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDto.Response> getOrdersByDateRange(Integer userId, String fromDate, String toDate) {
        LocalDate start = LocalDate.parse(fromDate);
        LocalDate end = LocalDate.parse(toDate);
        // [수정] OrderDatetime -> OrderDateTime (메서드명 변경 반영)
        List<OrderHeader> headers = orderHeaderRepository
                .findByUserIdAndOrderDateBetweenOrderByOrderDateTimeDesc(userId, start, end);
        return headers.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto.Response getOrderDetail(Integer userId, Integer orderId) {
        OrderHeader header = orderHeaderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("주문 내역이 없습니다."));
        if (!header.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 주문 내역만 조회할 수 있습니다.");
        }
        return mapToResponse(header);
    }

    @Transactional
    public void createCustomMenuFromOrder(Integer userId, Integer orderId, String customName) {
        log.info("주문 기반 커스텀 생성 요청: OrderId={}, Name={}", orderId, customName);
    }

    private OrderDto.Response mapToResponse(OrderHeader header) {
        String storeName = (header.getStore() != null) ? header.getStore().getName() : "알 수 없는 매장";

        // [수정] getOrderId() -> getId()
        List<OrderDetail> details = orderDetailRepository.findByOrderId(header.getId());
        List<OrderDto.OrderItemDetail> itemDtos = new ArrayList<>();

        int totalQuantity = 0;

        for (OrderDetail detail : details) {
            Menu menu = detail.getMenu();
            if (menu == null) continue;

            totalQuantity += detail.getQuantity();

            List<OrderDto.OptionItemDetail> optionDtos = new ArrayList<>();

            itemDtos.add(OrderDto.OrderItemDetail.builder()
                    // [수정] Menu의 getId() 확인
                    .menuId(menu.getId()) 
                    .menuName(menu.getName())
                    .quantity(detail.getQuantity())
                    .price(detail.getOrderDetailPrice().intValue())
                    .options(optionDtos)
                    .build());
        }

        String representativeName = "";
        if (!itemDtos.isEmpty()) {
            String firstMenuName = itemDtos.get(0).getMenuName();
            int otherCount = itemDtos.size() - 1;
            if (otherCount > 0) {
                representativeName = firstMenuName + " 외 " + otherCount + "건";
            } else {
                representativeName = firstMenuName;
            }
        }

        return OrderDto.Response.builder()
                // [수정] getOrderId() -> getId()
                .orderId(header.getId())
                .userId(header.getUserId())
                .storeId(header.getStore().getId())
                .storeName(storeName)
                // [수정] getOrderDatetime() -> getOrderDateTime()
                .orderDatetime(header.getOrderDateTime())
                .waitingNum(header.getWaitingNum())
                .totalPrice(header.getTotalPrice().intValue())
                .orderStatus(header.getOrderState().toString())
                .representativeMenuName(representativeName)
                .totalQuantity(totalQuantity)
                .items(itemDtos)
                .build();
    }
}