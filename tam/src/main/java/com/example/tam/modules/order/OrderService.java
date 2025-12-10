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
    private final CustomHeaderRepository customHeaderRepository;

    @Transactional
    public OrderDto.Response createOrder(Integer userId, OrderDto.CreateRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("매장을 찾을 수 없습니다."));

        OrderHeader header = OrderHeader.builder()
                .userId(userId)
                .store(store) // [수정] storeId(int) 대신 store(Object) 객체 주입
                .orderDatetime(LocalDateTime.now())
                .orderDate(LocalDate.now())
                .orderStatus("COMPLETED")
                .waitingNum(100)
                .totalPrice(0)
                .build();
        OrderHeader savedHeader = orderHeaderRepository.save(header);

        int totalPrice = 0;

        if (request.getItems() != null) {
            for (OrderDto.OrderItem item : request.getItems()) {
                Menu menu = menuRepository.findById(item.getMenuId())
                        .orElseThrow(() -> new ResourceNotFoundException("메뉴 없음: " + item.getMenuId()));

                int detailPrice = menu.getMenuPrice();
                
                OrderDetail detail = OrderDetail.builder()
                        .orderHeader(savedHeader) // [수정] orderId(int) 대신 orderHeader(Object) 객체 주입
                        .menu(menu)               // [수정] menuId(int) 대신 menu(Object) 객체 주입
                        .quantity(item.getQuantity())
                        .orderDetailPrice(0)
                        .build();
                OrderDetail savedDetail = orderDetailRepository.save(detail);

                if (item.getOptions() != null) {
                    for (OrderDto.OptionDetail opt : item.getOptions()) {
                        MenuOption menuOption = menuOptionRepository.findById(opt.getOptionId())
                                .orElseThrow(() -> new ResourceNotFoundException("옵션 없음: " + opt.getOptionId()));
                        
                        OrderOption orderOption = OrderOption.builder()
                                .orderDetail(savedDetail) // [수정] 객체 주입
                                .optionId(menuOption.getOptionId()) // 호환성 필드 사용
                                .extraNum(opt.getExtraNum())
                                .extraPrice(menuOption.getExtraPrice())
                                .build();
                        orderOptionRepository.save(orderOption);

                        detailPrice += (menuOption.getExtraPrice() * opt.getExtraNum());
                    }
                }

                int itemTotalPrice = detailPrice * item.getQuantity();
                savedDetail.setOrderDetailPrice(itemTotalPrice);
                orderDetailRepository.save(savedDetail);

                totalPrice += itemTotalPrice;
            }
        }

        savedHeader.setTotalPrice(totalPrice);
        orderHeaderRepository.save(savedHeader);

        log.info("주문 생성 완료: OrderId={}, TotalPrice={}", savedHeader.getOrderId(), totalPrice);

        return getOrderDetail(userId, savedHeader.getOrderId());
    }

    @Transactional(readOnly = true)
    public List<OrderDto.Response> getAllOrders(Integer userId) {
        List<OrderHeader> headers = orderHeaderRepository.findByUserIdOrderByOrderDatetimeDesc(userId);
        return headers.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDto.Response> getOrdersByDateRange(Integer userId, String fromDate, String toDate) {
        LocalDate start = LocalDate.parse(fromDate);
        LocalDate end = LocalDate.parse(toDate);
        List<OrderHeader> headers = orderHeaderRepository
                .findByUserIdAndOrderDateBetweenOrderByOrderDatetimeDesc(userId, start, end);
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
        // store 객체에서 직접 이름 가져오기
        String storeName = (header.getStore() != null) ? header.getStore().getStoreName() : "알 수 없는 매장";

        List<OrderDetail> details = orderDetailRepository.findByOrderId(header.getOrderId());
        List<OrderDto.OrderItemDetail> itemDtos = new ArrayList<>();

        int totalQuantity = 0;

        for (OrderDetail detail : details) {
            Menu menu = detail.getMenu();
            if (menu == null) continue;

            totalQuantity += detail.getQuantity() != null ? detail.getQuantity() : 0;


            List<OrderOption> options = orderOptionRepository.findByOrderDetailId(detail.getOrderDetailId());
            List<OrderDto.OptionItemDetail> optionDtos = options.stream()
                    .map(opt -> {
                        OptionGroup group = opt.getOptionGroup();
                        OptionValue value = opt.getOptionValue();

                        String optionClass;
                        String optionName;

                        if (group != null && value != null) {
                            optionClass = group.getName();
                            optionName  = value.getName();
                        } else {
                            optionClass = "기타";
                            optionName  = "알수없음";
                        }

                        Integer optionIdForClient = opt.getOptionId();
                        return OrderDto.OptionItemDetail.builder()
                                .optionId(optionIdForClient)
                                .optionClass(optionClass)
                                .optionName(optionName)
                                .extraPrice(opt.getExtraPrice())
                                .quantity(opt.getExtraNum())
                                .build();
                        })
                        .collect(Collectors.toList());

            itemDtos.add(OrderDto.OrderItemDetail.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .quantity(detail.getQuantity())
                    .price(detail.getOrderDetailPrice())
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
                .orderId(header.getOrderId())
                .userId(header.getUserId())
                .storeId(header.getStore().getStoreId()) // 객체에서 ID 추출
                .storeName(storeName)
                .orderDatetime(header.getOrderDatetime())
                .waitingNum(header.getWaitingNum())
                .totalPrice(header.getTotalPrice())
                .orderStatus(header.getOrderStatus())
                .representativeMenuName(representativeName)
                .totalQuantity(totalQuantity)
                .items(itemDtos)
                .build();
    }
}