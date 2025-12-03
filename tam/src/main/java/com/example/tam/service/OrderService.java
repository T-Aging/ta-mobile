package com.example.tam.modules.order;

import com.example.tam.common.entity.*;
import com.example.tam.dto.OrderDto;
import com.example.tam.modules.menu.MenuRepository;
import com.example.tam.modules.menu.MenuOptionRepository;
import com.example.tam.modules.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Transactional(readOnly = true)
    public List<OrderDto.Response> getAllOrders(Integer userId) {
        List<OrderHeader> headers = orderHeaderRepository.findByUserIdOrderByOrderDatetimeDesc(userId);
        
        return headers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDto.Response> getOrdersByDateRange(Integer userId, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(fromDate, formatter);
        LocalDate endDate = LocalDate.parse(toDate, formatter);

        List<OrderHeader> headers = orderHeaderRepository
                .findByUserIdAndOrderDateBetweenOrderByOrderDatetimeDesc(userId, startDate, endDate);
        
        return headers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto.Response getOrderDetail(Integer userId, Integer orderId) {
        OrderHeader header = orderHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다"));
        
        if (!header.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 주문만 조회할 수 있습니다");
        }
        
        return convertToResponse(header);
    }

    @Transactional
    public OrderDto.Response createOrder(Integer userId, OrderDto.CreateRequest request) {
        // OrderHeader 생성
        LocalDateTime now = LocalDateTime.now();
        OrderHeader header = OrderHeader.builder()
                .userId(userId)
                .storeId(request.getStoreId())
                .orderDatetime(now)
                .orderDate(now.toLocalDate())
                .waitingNum(generateWaitingNumber(now.toLocalDate()))
                .orderStatus("PENDING")
                .build();

        OrderHeader savedHeader = orderHeaderRepository.save(header);

        // OrderDetail 및 OrderOption 생성
        int totalPrice = 0;
        for (OrderDto.OrderItem item : request.getItems()) {
            Menu menu = menuRepository.findById(item.getMenuId())
                    .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));

            int itemPrice = menu.getMenuPrice() * item.getQuantity();

            // 옵션 가격 추가
            if (item.getOptions() != null) {
                for (OrderDto.OptionDetail option : item.getOptions()) {
                    MenuOption menuOption = menuOptionRepository.findById(option.getOptionId()).orElse(null);
                    if (menuOption != null && menuOption.getExtraPrice() != null) {
                        itemPrice += menuOption.getExtraPrice() * option.getExtraNum() * item.getQuantity();
                    }
                }
            }

            OrderDetail detail = OrderDetail.builder()
                    .orderId(savedHeader.getOrderId())
                    .menuId(item.getMenuId())
                    .quantity(item.getQuantity())
                    .orderDetailPrice(itemPrice)
                    .build();

            OrderDetail savedDetail = orderDetailRepository.save(detail);

            // OrderOption 저장
            if (item.getOptions() != null) {
                for (OrderDto.OptionDetail option : item.getOptions()) {
                    OrderOption orderOption = OrderOption.builder()
                            .orderDetailId(savedDetail.getOrderDetailId())
                            .optionId(option.getOptionId())
                            .extraNum(option.getExtraNum())
                            .build();
                    orderOptionRepository.save(orderOption);
                }
            }

            totalPrice += itemPrice;
        }

        // 총 가격 업데이트
        savedHeader.setTotalPrice(totalPrice);
        orderHeaderRepository.save(savedHeader);

        log.info("주문 생성 - userId: {}, orderId: {}, totalPrice: {}", userId, savedHeader.getOrderId(), totalPrice);
        return convertToResponse(savedHeader);
    }

    private int generateWaitingNumber(LocalDate date) {
        // 해당 날짜의 마지막 주문 번호 + 1
        List<OrderHeader> todayOrders = orderHeaderRepository
                .findByUserIdAndOrderDateBetweenOrderByOrderDatetimeDesc(null, date, date);
        return todayOrders.size() + 1;
    }

    private OrderDto.Response convertToResponse(OrderHeader header) {
        Store store = storeRepository.findById(header.getStoreId()).orElse(null);
        List<OrderDetail> details = orderDetailRepository.findByOrderId(header.getOrderId());

        List<OrderDto.OrderItemDetail> items = details.stream()
                .map(detail -> {
                    Menu menu = menuRepository.findById(detail.getMenuId()).orElse(null);
                    List<OrderOption> orderOptions = orderOptionRepository.findByOrderDetailId(detail.getOrderDetailId());

                    List<OrderDto.OptionDetail> options = orderOptions.stream()
                            .map(opt -> OrderDto.OptionDetail.builder()
                                    .optionId(opt.getOptionId())
                                    .extraNum(opt.getExtraNum())
                                    .build())
                            .collect(Collectors.toList());

                    return OrderDto.OrderItemDetail.builder()
                            .menuId(detail.getMenuId())
                            .menuName(menu != null ? menu.getMenuName() : null)
                            .quantity(detail.getQuantity())
                            .price(detail.getOrderDetailPrice())
                            .options(options)
                            .build();
                })
                .collect(Collectors.toList());

        return OrderDto.Response.builder()
                .orderId(header.getOrderId())
                .userId(header.getUserId())
                .storeId(header.getStoreId())
                .storeName(store != null ? store.getStoreName() : null)
                .orderDatetime(header.getOrderDatetime())
                .waitingNum(header.getWaitingNum())
                .totalPrice(header.getTotalPrice())
                .orderStatus(header.getOrderStatus())
                .items(items)
                .build();
    }
}
