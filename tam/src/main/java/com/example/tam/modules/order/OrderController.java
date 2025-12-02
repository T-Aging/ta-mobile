package com.example.tam.modules.order;

import com.example.tam.modules.order.entity.Order;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/t-age/users/{userid}/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    // 주문 내역 조회 (기본 / 상세 / 영수증)
    @GetMapping("/search")
    public ResponseEntity<?> getOrders(@PathVariable Long userid) {
        List<Order> orders = orderRepository.findByUserId(userid);
        return ResponseEntity.ok(orders);
    }

    // 기간 설정 및 기간 내 조회
    @PostMapping("/date-set")
    public ResponseEntity<?> getOrdersByDate(@PathVariable Long userid, @RequestBody DateRangeDto range) {
        // TODO: range.getStartDate(), range.getEndDate()로 DB 조회 구현
        return ResponseEntity.ok("기간 내 주문 목록 반환");
    }

    // 주문 내역으로 커스텀 메뉴 생성
    @PostMapping("/cus-gen")
    public ResponseEntity<?> createCustomFromOrder(@PathVariable Long userid, @RequestBody Map<String, Long> request) {
        Long orderId = request.get("orderId");
        // TODO: Order를 조회해서 그 옵션을 바탕으로 CustomMenu 저장 로직
        return ResponseEntity.ok("주문 내역 기반 커스텀 메뉴 생성 완료");
    }
    
    // DTO 클래스
    static class DateRangeDto {
        public String startDate;
        public String endDate;
    }
}
